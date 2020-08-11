/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */


package net.iGap.module;

import net.iGap.G;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.observers.interfaces.OnClientGetRoomHistoryResponse;
import net.iGap.observers.interfaces.OnMessageReceive;
import net.iGap.proto.ProtoClientGetRoomHistory;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestClientGetRoomHistory;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.proto.ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.DOWN;
import static net.iGap.proto.ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP;

public final class MessageLoader {

    private static final int LOCAL_LIMIT = 100;

    /**
     * fetch local message from RealmRoomMessage.
     * (hint : deleted message doesn't count)
     *
     * @param roomId           roomId that want show message for that
     * @param messageId        start query with this messageId
     * @param duplicateMessage if set true return message for messageId that used in this method (will be used "lessThanOrEqualTo") otherwise just return less or greater than messageId(will be used "lessThan" method)
     * @param direction        direction for load message up or down
     * @return Object[] ==> [0] -> ArrayList<StructMessageInfo>, [1] -> boolean hasMore, [2] -> boolean hasGap
     */
    public static Object[] getLocalMessage(Realm realm, long roomId, long messageId, long gapMessageId, boolean duplicateMessage, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        boolean hasMore = true;
        boolean hasSpaceToGap = true;
        List<RealmRoomMessage> realmRoomMessages;
        ArrayList<StructMessageInfo> structMessageInfos = new ArrayList<>();

        if (messageId == 0) {
            return new Object[]{structMessageInfos, false, false};
        }

        /**
         * get message from RealmRoomMessage
         */
        if (gapMessageId > 0) {

            if (direction == UP) {
                if (duplicateMessage) {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).between(RealmRoomMessageFields.MESSAGE_ID, gapMessageId, messageId).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                } else {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThan(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).between(RealmRoomMessageFields.MESSAGE_ID, gapMessageId, messageId).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                }
            } else {
                if (duplicateMessage) {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).between(RealmRoomMessageFields.MESSAGE_ID, messageId, gapMessageId).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                } else {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThan(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).between(RealmRoomMessageFields.MESSAGE_ID, messageId, gapMessageId).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                }
            }

        } else {

            if (direction == UP) {
                if (duplicateMessage) {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                } else {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThan(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                }

            } else {
                if (duplicateMessage) {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                } else {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThan(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                }
            }
        }


        /**
         * manage subList
         */
        if (realmRoomMessages.size() > LOCAL_LIMIT) {
            realmRoomMessages = realmRoomMessages.subList(0, LOCAL_LIMIT);
        } else {
            /**
             * when run this block means that end of message reached
             * and should be send request to server for get history
             */
            hasMore = false;
            hasSpaceToGap = false;
            realmRoomMessages = realmRoomMessages.subList(0, realmRoomMessages.size());
        }

        /**
         * convert message from RealmRoomMessage to StructMessageInfo for send to view
         */
        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
            if (realmRoomMessage.getMessageId() != 0) {
                structMessageInfos.add(new StructMessageInfo(realmRoomMessage));
            }
        }

        return new Object[]{structMessageInfos, hasMore, hasSpaceToGap};
    }


    //*********** get message from server

    public static String getOnlineMessage(final Realm realm, final long roomId, final long messageIdGetHistoryNotSafe, final long reachMessageId, int limit, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction, final OnMessageReceive onMessageReceive) {
        String requestId = new RequestClientGetRoomHistory().getRoomHistory(roomId, messageIdGetHistoryNotSafe, limit, direction, new RequestClientGetRoomHistory.IdentityClientGetRoomHistory(roomId, messageIdGetHistoryNotSafe, reachMessageId, direction));

        G.onClientGetRoomHistoryResponse = new OnClientGetRoomHistoryResponse() {
            @Override

            public void onGetRoomHistory(final long roomId, final long startMessageId, final long endMessageId, final long reachMessageId, long messageIdGetHistorySafe, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction historyDirection) {
                /**
                 * convert message from RealmRoomMessage to StructMessageInfo for send to view
                 */
                DbManager.getInstance().doRealmTask(new DbManager.RealmTask() {
                    @Override
                    public void doTask(Realm realm) {
                        boolean gapReached = false;
                        boolean jumpOverLocal = false;

                        if (UP == historyDirection) {
                            if (startMessageId <= reachMessageId) {
                                gapReached = true;
                                /**
                                 * if gapReached now check that future gap is reached or no. if future gap reached this means
                                 * that with get this history , client jumped from local messages and now is in another gap
                                 */
                                if (startMessageId <= (long) gapExist(realm, roomId, reachMessageId, UP)[0]) {
                                    jumpOverLocal = true;
                                }
                            }
                        } else {
                            if (endMessageId >= reachMessageId) {
                                gapReached = true;
                                /**
                                 * if gapReached now check that future gap is reached or no. if future gap reached this means
                                 * that with get this history , client jumped from local messages and now is in another gap
                                 */
                                if (endMessageId >= (long) gapExist(realm, roomId, reachMessageId, DOWN)[0]) {
                                    jumpOverLocal = true;
                                }
                            }
                        }

                        final boolean gapReachedFinal = gapReached;
                        final boolean jumpOverLocalFinal = jumpOverLocal;
                        long finalMessageId;
                        if (UP == historyDirection) {
                            finalMessageId = startMessageId;
                        } else {
                            finalMessageId = endMessageId;
                        }

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                /**
                                 * clear before state gap for avoid compute this message for gap state again
                                 */
                                clearGap(roomId, messageIdGetHistorySafe, finalMessageId, historyDirection, realm);

                                /**
                                 * if not reached to gap yet and exist reachMessageId
                                 * set new gap state for compute message for gap
                                 */
                                if (jumpOverLocalFinal || (!gapReachedFinal && reachMessageId > 0)) {
                                    setGap(finalMessageId, historyDirection, realm);
                                }
                            }
                        });

                        Sort sort;
                        if (historyDirection == UP) {
                            sort = Sort.DESCENDING;
                        } else {
                            sort = Sort.ASCENDING;
                        }

                        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).notEqualTo(RealmRoomMessageFields.DELETED, true).between(RealmRoomMessageFields.MESSAGE_ID, startMessageId, endMessageId).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, sort);
                        List<RealmRoomMessage> list = realm.copyFromRealm(realmRoomMessages);
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.refreshRealmUi();
                                onMessageReceive.onMessage(roomId, startMessageId, endMessageId, list, gapReachedFinal, jumpOverLocalFinal, historyDirection);
                            }
                        });
                    }
                });
            }

            @Override
            public void onGetRoomHistoryError(int majorCode, int minorCode, long messageIdGetHistory, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
                if (majorCode == 617) {
                    /**
                     * clear all gap state because not exist any more message
                     */
                    //realm.executeTransaction(new Realm.Transaction() {
                    //    @Override
                    //    public void execute(Realm realm) {
                    //
                    //        if (direction.equals(UP.toString())) {
                    //            RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).notEqualTo("previousMessageId", 0).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                    //            if (realmRoomMessages.size() > 0) {
                    //                realmRoomMessages.first().setPreviousMessageId(0);
                    //            }
                    //        } else {
                    //            RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).notEqualTo("futureMessageId", 0).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                    //            if (realmRoomMessages.size() > 0) {
                    //                realmRoomMessages.first().setFutureMessageId(0);
                    //            }
                    //        }
                    //    }
                    //});
                }

                onMessageReceive.onError(majorCode, minorCode, messageIdGetHistory, direction);
            }
        };

        return requestId;
    }

    //*********** detect gap in message

    /**
     * detect first RealmRoomMessage with previousMessageId and check
     * this previousMessageId exist in RealmRoomMessage or not
     * if gap exist this method will be returned reachedId.
     * reachedId will be used for calculate that after get clientGetRoomHistory
     * this history really reached to local message and gap filled or no
     *
     * @param roomId    roomId that want show message for that
     * @param messageId start query with this messageId
     * @param direction direction for load message up or down
     * @return [0] -> gapMessageId, [1] -> reachMessageId
     */
    public static Object[] gapExist(Realm realm, long roomId, long messageId, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        RealmRoomMessage realmRoomMessage = null;
        long gapMessageId = 0;
        long reachMessageId = 0;
        long checkMessageId = 0;

        /**
         * detect message that have previousMessageId or futureMessageId
         */
        if (direction == UP) {
            Number maxMessageId = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.PREVIOUS_MESSAGE_ID, 0).max(RealmRoomMessageFields.MESSAGE_ID);
            if (maxMessageId != null) {
                realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, (long) maxMessageId).findFirst();
                if (realmRoomMessage != null) {
                    checkMessageId = realmRoomMessage.getPreviousMessageId();
                }
            }
        } else {
            Number minMessageId = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.FUTURE_MESSAGE_ID, 0).min(RealmRoomMessageFields.MESSAGE_ID);
            if (minMessageId != null) {
                // add this for handle down gap is not set before up gap
                Number upGap = realm.where(RealmRoomMessage.class)
                        .equalTo(RealmRoomMessageFields.ROOM_ID, roomId)
                        .greaterThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId)
                        .notEqualTo(RealmRoomMessageFields.PREVIOUS_MESSAGE_ID, 0)
                        .min(RealmRoomMessageFields.MESSAGE_ID);
                if (upGap != null && upGap.longValue() < minMessageId.longValue()) {
                    Number downGapMessageId = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThan(RealmRoomMessageFields.MESSAGE_ID, upGap.longValue()).max(RealmRoomMessageFields.MESSAGE_ID);
                    if (downGapMessageId != null) {
                        DbManager.getInstance().doRealmTransaction(new DbManager.RealmTransaction() {
                            @Override
                            public void doTransaction(Realm realm) {
                                setGap(downGapMessageId.longValue(), DOWN, realm);
                            }
                        });
                        checkMessageId = downGapMessageId.longValue();
                        realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, downGapMessageId.longValue()).findFirst();
                    }
                } else {
                    realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, (long) minMessageId).findFirst();
                    if (realmRoomMessage != null) {
                        checkMessageId = realmRoomMessage.getFutureMessageId();
                    }
                }
            }
        }

        /**
         * check that exist any message with (message == checkMessageId) or not
         */
        if (realmRoomMessage != null) {
            RealmRoomMessage realmRoomMessageGap = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, checkMessageId).findFirst();

            /**
             * if any message with checkMessageId isn't exist in local so
             * client don't have this message and should get it from server
             */
            if (realmRoomMessageGap == null) {
                gapMessageId = checkMessageId;
            } else if (realmRoomMessageGap.getMessageId() == checkMessageId) {
                /**
                 * this step means that client insert checkMessageId in own message
                 */
                gapMessageId = checkMessageId;
            }
        }

        /**
         * if gap exist now detect reachMessageId
         * (query UP   ==> max of messageId that exist in local and also is lower than messageId that come in this method)
         * (query DOWN ==> min of messageId that exist in local and also is bigger than messageId that come in this method)
         */
        if (gapMessageId > 0) {
            if (direction == UP) {
                RealmQuery<RealmRoomMessage> realmRoomMessageRealmQuery = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThan(RealmRoomMessageFields.MESSAGE_ID, realmRoomMessage.getMessageId()).equalTo(RealmRoomMessageFields.PREVIOUS_MESSAGE_ID, 0);
                if (realmRoomMessageRealmQuery != null && realmRoomMessageRealmQuery.max(RealmRoomMessageFields.MESSAGE_ID) != null) {
                    reachMessageId = (long) realmRoomMessageRealmQuery.max(RealmRoomMessageFields.MESSAGE_ID);
                }

                if (reachMessageId == 0) {
                    RealmQuery<RealmRoomMessage> realmRoomMessageRealmQuery1 = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThan(RealmRoomMessageFields.MESSAGE_ID, realmRoomMessage.getMessageId());
                    if (realmRoomMessageRealmQuery1 != null && realmRoomMessageRealmQuery1.max(RealmRoomMessageFields.MESSAGE_ID) != null) {
                        reachMessageId = (long) realmRoomMessageRealmQuery1.max(RealmRoomMessageFields.MESSAGE_ID);
                    }
                }

            } else {
                RealmQuery<RealmRoomMessage> realmRoomMessageRealmQuery = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThan(RealmRoomMessageFields.MESSAGE_ID, realmRoomMessage.getMessageId()).equalTo(RealmRoomMessageFields.FUTURE_MESSAGE_ID, 0);
                if (realmRoomMessageRealmQuery != null && realmRoomMessageRealmQuery.min(RealmRoomMessageFields.MESSAGE_ID) != null) {
                    reachMessageId = (long) realmRoomMessageRealmQuery.min(RealmRoomMessageFields.MESSAGE_ID);
                }

                if (reachMessageId == 0) {
                    RealmQuery<RealmRoomMessage> realmRoomMessageRealmQuery1 = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThan(RealmRoomMessageFields.MESSAGE_ID, realmRoomMessage.getMessageId());
                    if (realmRoomMessageRealmQuery1 != null && realmRoomMessageRealmQuery1.min(RealmRoomMessageFields.MESSAGE_ID) != null) {
                        reachMessageId = (long) realmRoomMessageRealmQuery1.min(RealmRoomMessageFields.MESSAGE_ID);
                    }
                }
            }
        }


        return new Object[]{gapMessageId, reachMessageId};
    }


    /**
     * after each get history check all messages that are between first
     * and end message in history response and clear all gap state
     * <p>
     * (hint : don't need use from transaction)
     */
    private static void clearGap(final long roomId, final long messageId, final long finalMessageId, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction, Realm realm) {

        long fromPosition;
        long toPosition;

        if (direction == UP) {
            fromPosition = finalMessageId;
            toPosition = messageId;
        } else {
            fromPosition = messageId;
            toPosition = finalMessageId;
        }

        RealmResults<RealmRoomMessage> realmRoomMessages1 = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).between(RealmRoomMessageFields.MESSAGE_ID, fromPosition, toPosition).findAll();
        for (RealmRoomMessage realmRoomMessage : realmRoomMessages1) {
            realmRoomMessage.setPreviousMessageId(0);
            realmRoomMessage.setFutureMessageId(0);
        }
    }

    /**
     * check that this message have previous or future messageId
     *
     * @param direction set direction for detect previous or future
     */
    private static boolean isGap(Realm realm, long messageId, String direction) {

        RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
        if (realmRoomMessage != null) {
            if (direction.equals(UP.toString())) {
                return realmRoomMessage.getPreviousMessageId() != 0;
            } else {
                return realmRoomMessage.getFutureMessageId() != 0;
            }
        }

        return false;
    }

    /**
     * set new gap state for UP or DOWN state
     * <p>
     * (hint : don't need use from transaction)
     *
     * @param messageId message that want set gapMessageId to that
     */
    private static void setGap(final long messageId, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction, Realm realm) {
        RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
        if (realmRoomMessage != null) {
            if (direction == UP) {
                realmRoomMessage.setPreviousMessageId(messageId);
            } else {
                realmRoomMessage.setFutureMessageId(messageId);
            }
        }
    }




    /**
     * change direction string to ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction
     */
    public static ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction convertDirection(String direction) {
        if (direction.equals(UP.toString())) {
            return UP;
        }
        return DOWN;
    }
}