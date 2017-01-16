package eu.peppol.outbound.transmission;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import eu.peppol.lang.OxalisTransmissionException;
import no.difi.oxalis.api.outbound.MessageSender;
import no.difi.vefa.peppol.common.model.TransportProfile;
import no.difi.vefa.peppol.mode.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory orchestrating available implementations of transport profiles.
 *
 * @author erlend
 */
class MessageSenderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderFactory.class);

    /**
     * Guice injector used to load MessageSender implementation when needed, allows use of non-singleton
     * implementations. It is not considered best practice to inject injector like this, however in this case is this
     * suitable based on our requirements.
     */
    private final Injector injector;

    /**
     * Map of configurations for supported transport profiles.
     */
    private final Map<TransportProfile, Config> configMap;

    /**
     * Prioritized list of supported transport profiles.
     */
    private final List<TransportProfile> prioritizedTransportProfiles;

    @Inject
    public MessageSenderFactory(Injector injector, Mode mode) {
        this.injector = injector;
        Config config = mode.getConfig();

        // Construct map of configuration for detected transport profiles.
        configMap = config.getObject("transport").keySet().stream()
                .map(key -> config.getConfig(String.format("transport.%s", key)))
                .collect(Collectors.toMap(c -> TransportProfile.of(c.getString("profile")), Function.identity()));

        // Create prioritized list of transport profiles.
        prioritizedTransportProfiles = Collections.unmodifiableList(configMap.values().stream()
                .filter(o -> !o.hasPath("enabled") || o.getBoolean("enabled"))
                .sorted((o1, o2) -> Integer.compare(o2.getInt("weight"), o1.getInt("weight")))
                .map(o -> o.getString("profile"))
                .map(TransportProfile::of)
                .collect(Collectors.toList()));

        // Logging list of prioritized transport profiles supported.
        LOGGER.info("Prioritized list of transport profiles: {}", prioritizedTransportProfiles);
    }

    /**
     * Fetch list of supported transport profiles ordered by priority.
     *
     * @return List of supported transport profiles.
     */
    public List<TransportProfile> getPrioritizedTransportProfiles() {
        return prioritizedTransportProfiles;
    }

    /**
     * Fetch identifier used in named annotation for the implementation of requested transport profile.
     *
     * @param transportProfile Identifier of transport profile requested.
     * @return String used in the named annotation.
     * @throws OxalisTransmissionException Thrown when transport profile is not supported.
     */
    public String getSender(TransportProfile transportProfile) throws OxalisTransmissionException {
        if (!configMap.containsKey(transportProfile))
            throw new OxalisTransmissionException(
                    String.format("Transport protocol '%s' not supported.", transportProfile.getValue()));

        return configMap.get(transportProfile).getString("sender");
    }

    /**
     * Fetch MessageSender implementing from provided transport profile.
     *
     * @param transportProfile Identifier of transport profile used to fetch MessageSender.
     * @return MessageSender implementing the transport profile requested.
     * @throws OxalisTransmissionException Thrown when loading of implementation fails or implementation is not found.
     */
    public MessageSender getMessageSender(TransportProfile transportProfile) throws OxalisTransmissionException {
        return injector.getInstance(
                Key.get(MessageSender.class, Names.named(getSender(transportProfile))));
    }
}
