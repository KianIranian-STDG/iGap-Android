package net.iGap.libs.emojiKeyboard.emoji;

import net.iGap.libs.emojiKeyboard.emoji.category.ActivityCategory;
import net.iGap.libs.emojiKeyboard.emoji.category.AnimalsAndNatureCategory;
import net.iGap.libs.emojiKeyboard.emoji.category.EmojiCategory;
import net.iGap.libs.emojiKeyboard.emoji.category.FlagsCategory;
import net.iGap.libs.emojiKeyboard.emoji.category.FoodAndDrinkCategory;
import net.iGap.libs.emojiKeyboard.emoji.category.ObjectsCategory;
import net.iGap.libs.emojiKeyboard.emoji.category.SmileysAndPeopleCategory;
import net.iGap.libs.emojiKeyboard.emoji.category.SymbolsCategory;
import net.iGap.libs.emojiKeyboard.emoji.category.TravelAndPlaceCategory;

import java.util.Collections;
import java.util.HashSet;

public class EmojiCategoryManager {
    private static EmojiCategoryManager instance;

    private static EmojiCategory[] emojiCategories;
    public static HashSet<String> coloredEmojiMap;

    public static EmojiCategoryManager getInstance() {
        if (instance == null) {
            instance = new EmojiCategoryManager();

            coloredEmojiMap = new HashSet<>(coloredEmojies.length);

            Collections.addAll(coloredEmojiMap, coloredEmojies);

            emojiCategories = new EmojiCategory[]{
                    new SmileysAndPeopleCategory(),
                    new AnimalsAndNatureCategory(),
                    new FoodAndDrinkCategory(),
                    new ActivityCategory(),
                    new TravelAndPlaceCategory(),
                    new ObjectsCategory(),
                    new SymbolsCategory(),
                    new FlagsCategory()
            };
        }


        return instance;
    }

    public EmojiCategory[] getEmojiCategory() {
        return emojiCategories;
    }

    public int getCategorySize() {
        return EmojiManager.getInstance().EMOJI_CATEGORY_SIZE;
    }

    private static String[] coloredEmojies = {
            "🤲", "👐", "🙌", "👏", "👍", "👎", "👊", "✊", "🤛", "🤜", "🤞", "✌", "🤟", "🤘",
            "👌", "🤏", "👈", "👉", "👆", "👇", "☝", "✋", "🤚", "🖐", "🖖", "👋", "🤙", "💪",
            "🖕", "✍", "🙏", "🦶", "🦵", "👂", "🦻", "👃", "👶", "👧", "🧒", "👦", "👩",
            "🧑", "👨", "👩‍🦱", "🧑‍🦱", "👨‍🦱", "👩‍🦰", "🧑‍🦰", "👨‍🦰", "👱‍♀", "👱", "👱‍♂", "👩‍🦳", "🧑‍🦳", "👨‍🦳",
            "👩‍🦲", "🧑‍🦲", "👨‍🦲", "🧔", "👵", "🧓", "👴", "👲", "👳‍♀", "👳", "👳‍♂", "🧕", "👮‍♀", "👮", "👮‍♂", "👷‍♀",
            "👷", "👷‍♂", "💂‍♀", "💂", "💂‍♂", "🕵‍♀", "🕵", "🕵‍♂", "👩‍⚕", "🧑‍⚕", "👨‍⚕", "👩‍🌾", "🧑‍🌾", "👨‍🌾", "👩‍🍳", "🧑‍🍳",
            "👨‍🍳", "👩‍🎓", "🧑‍🎓", "👨‍🎓", "👩‍🎤", "🧑‍🎤", "👨‍🎤", "👩‍🏫", "🧑‍🏫", "👨‍🏫", "👩‍🏭", "🧑‍🏭", "👨‍🏭", "👩‍💻", "🧑‍💻", "👨‍💻",
            "👩‍💼", "🧑‍💼", "👨‍💼", "👩‍🔧", "🧑‍🔧", "👨‍🔧", "👩‍🔬", "🧑‍🔬", "👨‍🔬", "👩‍🎨", "🧑‍🎨", "👨‍🎨", "👩‍🚒", "🧑‍🚒", "👨‍🚒", "👩‍✈",
            "🧑‍✈", "👨‍✈", "👩‍🚀", "🧑‍🚀", "👨‍🚀", "👩‍⚖", "🧑‍⚖", "👨‍⚖", "👰", "🤵", "👸", "🤴", "🦸‍♀", "🦸", "🦸‍♂", "🦹‍♀",
            "🦹", "🦹‍♂", "🤶", "🎅", "🧙‍♀", "🧙", "🧙‍♂", "🧝‍♀", "🧝", "🧝‍♂", "🧛‍♀", "🧛", "🧛‍♂", "🧟‍♀", "🧜‍♀", "🧜",
            "🧜‍♂", "🧚‍♀", "🧚", "🧚‍♂", "👼", "🤰", "🤱", "🙇‍♀", "🙇", "🙇‍♂", "💁‍♀", "💁", "💁‍♂", "🙅‍♀", "🙅", "🙅‍♂",
            "🙆‍♀", "🙆", "🙆‍♂", "🙋‍♀", "🙋", "🙋‍♂", "🧏‍♀", "🧏", "🧏‍♂", "🤦‍♀", "🤦", "🤦‍♂", "🤷‍♀", "🤷", "🤷‍♂", "🙎‍♀",
            "🙎", "🙎‍♂", "🙍‍♀", "🙍", "🙍‍♂", "💇‍♀", "💇", "💇‍♂", "💆‍♀", "💆", "💆‍♂", "🧖‍♀", "🧖", "🧖‍♂", "💅", "🤳",
            "💃", "🕺", "🕴", "👩‍🦽", "🧑‍🦽", "👨‍🦽", "👩‍🦼", "🧑‍🦼", "👨‍🦼", "🚶‍♀", "🚶", "🚶‍♂", "👩‍🦯", "🧑‍🦯", "👨‍🦯", "🧎‍♀",
            "🧎", "🧎‍♂", "🏃‍♀", "🏃", "🏃‍♂", "🧍‍♀", "🧍", "🧍‍♂", "🏋‍♀", "🏋", "🏋‍♂", "🤸‍♀", "🤸", "🤸‍♂", "⛹‍♀", "⛹",
            "⛹‍♂", "🤾‍♀", "🤾", "🤾‍♂", "🏌‍♀", "🏌", "🏌‍♂", "🏇", "🧘‍♀", "🧘", "🧘‍♂", "🏄‍♀", "🏄", "🏄‍♂", "🏊‍♀", "🏊",
            "🏊‍♂", "🤽‍♀", "🤽", "🤽‍♂", "🚣‍♀", "🚣", "🚣‍♂", "🧗‍♀", "🧗", "🧗‍♂", "🚵‍♀", "🚵", "🚵‍♂", "🚴‍♀", "🚴", "🚴‍♂",
            "🤹‍♀", "🤹", "🤹‍♂", "🛀"
    };
}
