package com.template.lib.util;


import com.template.lib.support.Props;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * Created by velichko-aa on 01.06.2015.
 */
public class Mail {
    public final static String server = Props.get("mail.server");
    public final static String storeType = "imaps";

    public String getMessageText(final String user, final String password, boolean deleteMessages,
                                 final String mailSubject) {
        String result = null;
        Message[] messages = null;
        try {
            Thread.sleep(10 * 1000);
            Session emailSession = Session.getDefaultInstance(new Properties(), null);
            Store emailStore = emailSession.getStore(storeType);
            emailStore.connect(server, user, password);

            Folder emailFolder = emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);
            messages = emailFolder.getMessages();

            //wait messages
            int tryCount = 0;
            boolean exist = false;
            while (tryCount < 3 && !exist) {
                if (messages.length == 0) {
                    emailStore.close();
                    Thread.sleep(20 * 1000);
                    emailStore.connect(server, user, password);
                    emailFolder = emailStore.getFolder("INBOX");
                    emailFolder.open(Folder.READ_WRITE);
                    messages = emailFolder.getMessages();
                } else {
                    for (int i = 0; i < messages.length; i++) {
                        if (!messages[i].getSubject().contains(mailSubject))
                            continue;

                        exist = true;
                        if (messages[i].getContent() instanceof Multipart) {
                            Multipart multipart = (Multipart) messages[i].getContent();
                            if (multipart.getBodyPart(0).getContent() instanceof MimeMultipart) {
                                MimeMultipart multi = (MimeMultipart) multipart.getBodyPart(0).getContent();
                                result = multi.getBodyPart(0).getContent().toString();
                            } else {
                                BodyPart bodyPart = multipart.getBodyPart(0);
                                result = bodyPart.getContent().toString();
                            }
                        } else {
                            Message message = messages[i];
                            result = message.getContent().toString();
                        }
                    }
                    break;
                }
                tryCount++;
            }
            if (deleteMessages) {
                for (int i = 0; i < messages.length; i++) {
                    Message message = messages[i];
                    if (deleteMessages)
                        message.setFlag(Flags.Flag.DELETED, true);
                }
            }
            emailFolder.close(deleteMessages);
            emailStore.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void deleteAllMessages(final String user, final String password) {
        Message[] messages = null;
        try {
            Session emailSession = Session.getDefaultInstance(new Properties(), null);
            Store emailStore = emailSession.getStore(storeType);
            emailStore.connect(server, user, password);
            Folder emailFolder = emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);
            messages = emailFolder.getMessages();
            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                message.setFlag(Flags.Flag.DELETED, true);
            }
            emailFolder.close(true);
            emailStore.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int checkMailsCount(final String user, final String password) {
        Message[] messages = null;
        try {
            Thread.sleep(10 * 1000);
            Session emailSession = Session.getDefaultInstance(new Properties(), null);
            Store emailStore = emailSession.getStore(storeType);
            emailStore.connect(server, user, password);
            Folder emailFolder = emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);
            messages = emailFolder.getMessages();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages.length;
    }

    public int checkMailsCount(final String user, final String password, String subject) {
        Message[] messages = null;
        int count = 0;
        try {
            Thread.sleep(10 * 1000);
            Session emailSession = Session.getDefaultInstance(new Properties(), null);
            Store emailStore = emailSession.getStore(storeType);
            emailStore.connect(server, user, password);
            Folder emailFolder = emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);
            messages = emailFolder.getMessages();
            for (Message msg : messages) {
                if (msg.getSubject().contains(subject)) {
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
