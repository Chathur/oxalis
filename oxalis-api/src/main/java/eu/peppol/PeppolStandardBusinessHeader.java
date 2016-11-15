/*
 * Copyright (c) 2010 - 2015 Norwegian Agency for Pupblic Government and eGovernment (Difi)
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

package eu.peppol;

import eu.peppol.identifier.MessageId;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolDocumentTypeId;
import eu.peppol.identifier.PeppolProcessTypeId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Our representation of the SBDH (Standard Business Document Header), which makes us
 * independent of the StandardBusinessDocumentHeader generated by JAXB. Furthermore
 * the UN/CEFACT SBDH is kind of awkward to use as some of the elements of interest to us,
 * are split into several fields.
 *
 * @author steinar
 * @author thore
 */
public class PeppolStandardBusinessHeader {

    /**
     * Peppol Participant Identification for the recipient
     */
    private ParticipantId recipientId;

    /**
     * Peppol Participant Identification for the sender
     */
    private ParticipantId senderId;

    /**
     * The type of document to send
     */
    private PeppolDocumentTypeId peppolDocumentTypeId;

    /**
     * The business process this document is a part of
     */
    private PeppolProcessTypeId profileTypeIdentifier;

    /**
     * Represents the unique identity of the message envelope, this is only used by the AS2 protocol and
     * identifies the AS2 envelope (SBDH).  Upon resending the same SBDH, the same messageId can be used.
     *
     * This messageId is not the same as the "AS2 Message-ID" or the "START message id", which really are
     * unique "transmission id's" that should be unique for each transmission.
     *
     * <code>//StandardBusinessDocumentHeader/DocumentIdentification/InstanceIdentifier</code>
     */
    private MessageId messageId;

    private Date creationDateAndTime;

    /**
     * Set the time to current and makes a random MessageId as default
     */
    public static PeppolStandardBusinessHeader createPeppolStandardBusinessHeaderWithNewDate() {
        PeppolStandardBusinessHeader p = new PeppolStandardBusinessHeader();
        p.setCreationDateAndTime(new Date());
        return p;
    }

    /**
     * Empty constructor, no defaults - all must be supplied by user
     */
    public PeppolStandardBusinessHeader() {
        /* intentionally nothing */
    }

    /**
     * Copy constructor
     */
    public PeppolStandardBusinessHeader(PeppolStandardBusinessHeader peppolStandardBusinessHeader) {
        recipientId = peppolStandardBusinessHeader.getRecipientId();
        senderId = peppolStandardBusinessHeader.getSenderId();
        peppolDocumentTypeId = peppolStandardBusinessHeader.getDocumentTypeIdentifier();
        profileTypeIdentifier = peppolStandardBusinessHeader.getProfileTypeIdentifier();
        messageId = peppolStandardBusinessHeader.getMessageId();
        creationDateAndTime = peppolStandardBusinessHeader.getCreationDateAndTime();
    }

    /**
     * Do we have enough transport details to send the message?
     * @return true if transport details are complete.
     */
    public boolean isComplete() {
        return (
                (recipientId != null) &&
                (senderId != null) &&
                (peppolDocumentTypeId != null) &&
                (profileTypeIdentifier != null) &&
                (messageId != null) &&
                (creationDateAndTime != null)
               );
    }

    /**
     * Returns a list of property names that are still missing.
     * @return empty list if headers are complete
     */
    public List<String> listMissingProperties() {
        List<String> mhf = new ArrayList<String>();
        if (recipientId == null) mhf.add("recipientId");
        if (senderId == null) mhf.add("senderId");
        if (peppolDocumentTypeId == null) mhf.add("peppolDocumentTypeId");
        if (profileTypeIdentifier == null) mhf.add("profileTypeIdentifier");
        if (messageId == null) mhf.add("messageId");
        if (creationDateAndTime == null) mhf.add("creationDateAndTime");
        return mhf;
    }

    public void setRecipientId(ParticipantId recipientId) {
        this.recipientId = recipientId;
    }

    public ParticipantId getRecipientId() {
        return recipientId;
    }

    public void setSenderId(ParticipantId senderId) {
        this.senderId = senderId;
    }

    public ParticipantId getSenderId() {
        return senderId;
    }

    public void setMessageId(MessageId messageId) {
        this.messageId = messageId;
    }

    public MessageId getMessageId() {
        return messageId;
    }

    public void setCreationDateAndTime(Date creationDateAndTime) {
        this.creationDateAndTime = creationDateAndTime;
    }

    public Date getCreationDateAndTime() {
        return creationDateAndTime;
    }

    public void setDocumentTypeIdentifier(PeppolDocumentTypeId documentTypeIdentifier) {
        this.peppolDocumentTypeId = documentTypeIdentifier;
    }

    public PeppolDocumentTypeId getDocumentTypeIdentifier() {
        return peppolDocumentTypeId;
    }

    public void setProfileTypeIdentifier(PeppolProcessTypeId profileTypeIdentifier) {
        this.profileTypeIdentifier = profileTypeIdentifier;
    }

    public PeppolProcessTypeId getProfileTypeIdentifier() {
        return profileTypeIdentifier;
    }

}
