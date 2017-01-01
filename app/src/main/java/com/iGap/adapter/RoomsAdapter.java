package com.iGap.adapter;

import com.iGap.adapter.items.RoomItem;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class RoomsAdapter<Item extends RoomItem> extends FastItemAdapter<Item> {
    public static List<Long> userInfoAlreadyRequests = new ArrayList<>();

    public RoomsAdapter() {
        // as we provide id's for the items we want the hasStableIds enabled to speed up things
        setHasStableIds(true);
    }

   /* public void downloadingAvatarThumbnail(String token) {
        for (Item item : getAdapterItems()) {
            if (item.mInfo.getAvatar() != null && item.mInfo.getAvatar().getFile().getToken() != null && item.mInfo.getAvatar().getFile().getToken().equalsIgnoreCase(token)) {
                item.onRequestDownloadAvatarThumbnail(token, true);
                notifyAdapterDataSetChanged();
                break;
            }
        }
    }*/

    public void updateChat(long chatId, Item item) {
        List<Item> items = getAdapterItems();
        for (Item chat : items) {
            if (chat.mInfo.getId() == chatId) {
                int pos = items.indexOf(chat);
                remove(pos);
                add(0, item);
                break;
            }
        }
    }

    public void updateChatStatus(long chatId, final String status) {
        List<Item> items = getAdapterItems();
        Realm realm = Realm.getDefaultInstance();
        for (final Item chat : items) {
            if (chat.mInfo.getId() == chatId) {
                final int pos = items.indexOf(chat);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, chat.mInfo.getLastMessage().getMessageId()).findFirst().setStatus(status);
                        notifyAdapterItemChanged(pos);
                    }
                });
                break;
            }
        }
        realm.close();
    }

    public void goToTop(long chatId) {
        Item item = null;
        List<Item> items = getAdapterItems();
        for (Item chat : items) {
            if (chat.mInfo.getId() == chatId) {
                item = chat;
                break;
            }
        }
        updateChat(chatId, item);
    }

    public void notifyDraft(long chatId, final String draftMessage) {
        List<Item> items = getAdapterItems();
        for (final Item chat : items) {
            if (chat.mInfo.getId() == chatId) {

                final int position = items.indexOf(chat);

                // because of nested transactions, following lines should not be into a transaction method
                chat.mInfo.getDraft().setMessage(draftMessage);
                notifyItemChanged(position);
            }
        }
    }

    public void notifyWithRoomId(long chatId) {
        List<Item> items = getAdapterItems();
        for (final Item chat : items) {
            if (chat.mInfo.getId() == chatId) {
                notifyItemChanged(items.indexOf(chat));
            }
        }
    }

    public boolean existRoom(long roomId) {
        List<Item> items = getAdapterItems();
        for (final Item chat : items) {
            if (chat.mInfo.getId() == roomId) {
                return true;
            }
        }
        return false;
    }

    public int getPosition(long chatId) {
        List<Item> items = getAdapterItems();
        for (final Item chat : items) {
            if (chat.mInfo.getId() == chatId) {
                return items.indexOf(chat);
            }
        }
        return -1;
    }

    /**
     * check realm for detect that item is exist or changed
     * info with another thread
     *
     * @param realmRoom
     * @return true if is valid and is exist otherwise return false
     */
    public boolean checkValidationForRealm(RealmRoom realmRoom) {
        if (realmRoom != null && realmRoom.isValid() && !realmRoom.isDeleted()) {
            return true;
        }
        return false;
    }

    /*public void setAction(long roomId, ProtoGlobal.ClientAction clientAction) {
        List<Item> items = getAdapterItems();
        for (final Item chat : items) {
            if (chat.mInfo.getId() == roomId) {
                String action = HelperGetAction.getAction(chat.getInfo().getType(), clientAction);

                if (action != null) {
                    chat.getInfo().setActionState(action);
                    notifyItemChanged(items.indexOf(chat));
                } else {
                    chat.getInfo().setActionState(null);
                    notifyItemChanged(items.indexOf(chat));
                }
                break;
            }
        }
    }*/

    /*public void setAction(long roomId, ProtoGlobal.ClientAction clientAction) {
        List<Item> items = getAdapterItems();
        for (final Item chat : items) {
            if (chat.mInfo.getId() == roomId) {
                String action = HelperGetAction.getAction(chat.getInfo().getType(), clientAction);

                if (action != null) {
                    //chat.getInfo().setActionState(action);
                    chat.action = action;
                    notifyItemChanged(items.indexOf(chat));
                } else {
                    //chat.getInfo().setActionState(null);
                    chat.action = null;
                    notifyItemChanged(items.indexOf(chat));
                }
                break;
            }
        }
    }*/


}