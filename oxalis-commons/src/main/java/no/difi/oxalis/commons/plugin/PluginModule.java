/*
 * Copyright (c) 2010 - 2017 Norwegian Agency for Public Government and eGovernment (Difi)
 *
 * This file is part of Oxalis.
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission
 * - subsequent versions of the EUPL (the "Licence"); You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the Licence
 *  is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and limitations under the Licence.
 *
 */

package no.difi.oxalis.commons.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import no.difi.oxalis.api.persist.PayloadPersister;
import no.difi.oxalis.commons.persist.DefaultPersister;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author steinar
 *         Date: 24.01.2017
 *         Time: 10.43
 */
public class PluginModule extends AbstractModule {

    @Override
    protected void configure() {
        // Makes this class available to the PluginProvider
        bind(Key.get(PayloadPersister.class, Names.named("default"))).to(DefaultPersister.class);
    }

    @Provides
    @Singleton
    @Named("oxalis.ext.dir")
    public Path providesPath() {
        return Paths.get(System.getProperty("java.home"), "lib");
    }

    @Provides
    public Provider<PayloadPersister> payloadPersisterProvider() {
        return new PluginProvider<>(null, null, PayloadPersister.class);
    }
}
