package net.iGap.emojiKeyboard.dataLayer;

import net.iGap.repository.sticker.StickerRepository;

public class EmojiViewDataLayer {
    private StickerRepository stickerRepository;


    public EmojiViewDataLayer() {
        this.stickerRepository = new StickerRepository();
    }

    public void updateSticker() {
        stickerRepository.putOrUpdateMyStickerPackToDb();
    }


}
