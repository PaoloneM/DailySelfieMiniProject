package com.paolone.dailyselfie.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    public static String[] groupData;
    public static String[][] childData;

    static {
        // Add 3 sample items.
        addItem(new DummyItem("1", "Item 1"));
        addItem(new DummyItem("2", "Item 2"));
        addItem(new DummyItem("3", "Item 3"));

        groupData = new String[] { "Test Header 1", "Test Header 2", "Test Header 3", "Test Header 4" };

        childData = new String [][] {
                { "Item 1-1","Item 1-2", "Item 1-3", "Item 1-4", "Item 1-5"},
                { "Item 2-1","Item 2-2", "Item 2-3", "Item 2-4", "Item 2-5"},
                { "Item 3-1","Item 3-2", "Item 3-3", "Item 3-4", "Item 3-5"},
                { "Item 4-1","Item 4-2", "Item 4-3", "Item 4-4", "Item 4-5"},
        };

    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String content;

        public DummyItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
