/*
 * Copyright 2010-2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.oxalis.as2.model;

import no.difi.oxalis.as2.code.As2Header;

import javax.mail.internet.InternetHeaders;
import java.util.Date;

import static no.difi.oxalis.as2.util.HeaderUtil.getFirstValue;

/**
 * Holds the data in a Message Disposition Notification (MDN).
 * Instances of this class must be transformed into a MIME message for transmission.
 *
 * @author steinar
 *         Date: 09.10.13
 *         Time: 21:01
 */
public class MdnData {

    public static final String SUBJECT = "AS2 MDN as you requested";

    private final String subject;

    private final String as2From;

    private final String as2To;

    private final As2Disposition as2Disposition;

    private final Mic mic;

    private Date receptionTimeStamp;

    private String messageId;

    private MdnData(Builder builder) {
        this.subject = builder.subject;
        this.as2From = builder.as2From;
        this.as2To = builder.as2To;
        this.as2Disposition = builder.disposition;
        this.mic = builder.mic;
        this.receptionTimeStamp = builder.date;
        this.messageId = builder.messageId;
        this.receptionTimeStamp = new Date();
    }

    public String getSubject() {
        return subject;
    }

    public String getAs2From() {
        return as2From;
    }

    public String getAs2To() {
        return as2To;
    }

    public As2Disposition getAs2Disposition() {
        return as2Disposition;
    }

    public Mic getMic() {
        return mic;
    }

    public Date getReceptionTimeStamp() {
        return receptionTimeStamp;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MdnData{");
        sb.append("subject='").append(subject).append('\'');
        sb.append(", as2From='").append(as2From).append('\'');
        sb.append(", as2To='").append(as2To).append('\'');
        sb.append(", as2Disposition=").append(as2Disposition);
        sb.append(", mic=").append(mic);
        sb.append(", receptionTimeStamp=").append(receptionTimeStamp);
        sb.append(", messageId='").append(messageId).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {

        String subject = "No subject";

        String as2From = "No_AS2From";

        String as2To = "No_AS2To";

        As2Disposition disposition;

        Mic mic = new Mic("", "");

        Date date = new Date();

        String messageId = "";

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder subject(String subject) {
            if (subject != null) {
                this.subject = subject;
            }
            return this;
        }

        public Builder as2From(String as2From) {
            this.as2From = as2From;
            return this;
        }

        public Builder as2To(String as2To) {
            this.as2To = as2To;
            return this;
        }

        public Builder disposition(As2Disposition disposition) {
            this.disposition = disposition;
            return this;
        }

        public Builder mic(Mic mic) {
            this.mic = mic;
            return this;
        }

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public MdnData build() {
            required(as2From, "as2From");
            required(as2To, "as2To");
            required(disposition, "disposition");
            return new MdnData(this);
        }

        private void required(Object object, String name) {
            if (object == null) {
                throw new IllegalStateException("Required property '" + name + "' not set.");
            }
        }

        public static MdnData buildProcessedOK(InternetHeaders headers, Mic mic) {
            Builder builder = new Builder();
            builder.disposition(As2Disposition.processed()).mic(mic);
            addStandardHeaders(headers, builder);
            return new MdnData(builder);
        }

        public static MdnData buildFailureFromHeaders(InternetHeaders map, String msg) {
            Builder builder = new Builder();
            builder.disposition(As2Disposition.failed(msg));
            addStandardHeaders(map, builder);
            return new MdnData(builder);
        }

        public static MdnData buildProcessingErrorFromHeaders(InternetHeaders headers, Mic mic, String msg) {
            Builder builder = new Builder();
            builder.disposition(As2Disposition.processedWithError(msg)).mic(mic);
            addStandardHeaders(headers, builder);
            return new MdnData(builder);
        }

        private static void addStandardHeaders(InternetHeaders headers, Builder builder) {
            builder.as2From(getFirstValue(headers, As2Header.AS2_TO))
                    .as2To(getFirstValue(headers, As2Header.AS2_FROM))
                    .date(new Date())
                    .subject(SUBJECT)
                    .messageId(getFirstValue(headers, As2Header.MESSAGE_ID));
        }
    }
}
