package me.jackint0sh.timedfly.flygui;

import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FlyInventory {

    private Inventory inventory;
    private Map<Integer, Item> items = new HashMap<>();
    public static Map<String, FlyInventory> inventories = new HashMap<>();
    private Consumer<InventoryCloseEvent> closeConsumer;

    public FlyInventory(int rows) {
        this(rows, null);
    }

    public FlyInventory(int rows, String title) {
        if (rows > 6) throw new IndexOutOfBoundsException("The number of rows should be be from 0-6.");
        this.inventory = Bukkit.createInventory(null, rows * 9, MessageUtil.color(title));
        FlyInventory.inventories.put(MessageUtil.color(title), this);
    }

    public FlyInventory(InventoryType inventoryType) {
        this(inventoryType, null);
    }

    public FlyInventory(InventoryType inventoryType, String title) {
        this.inventory = Bukkit.createInventory(null, inventoryType, MessageUtil.color(title));
        FlyInventory.inventories.put(MessageUtil.color(title), this);
    }

    public FlyInventory addItem(Item itemStack) {
        this.inventory.addItem(itemStack);
        this.items.put(items.size() - (items.size() == 0 ? 0 : 1), itemStack);
        return this;
    }

    public FlyInventory addItems(Item... itemStack) {
        this.inventory.addItem(itemStack);
        for (Item item : itemStack) this.items.put(items.size() - (items.size() == 0 ? 0 : 1), item);
        return this;
    }

    public void setItem(Item itemStack, int slot) {
        this.inventory.setItem(slot, itemStack);
        this.items.put(slot, itemStack);
    }

    public void setItem(Item itemStack, int x, int y) {
        if (x > 9) throw new IndexOutOfBoundsException("Coordinate X should be be from 0-9.");
        if (y > 6) throw new IndexOutOfBoundsException("Coordinate Y should be be from 0-6.");

        int location = x + (y * 9);

        if (this.inventory.getSize() < location)
            throw new IndexOutOfBoundsException("Could not find the location provided on the inventory.");

        this.inventory.setItem(location, itemStack);
        this.items.put(location, itemStack);
    }

    public FlyInventory setItems(Item... itemStack) {
        this.items.clear();
        for (int i = 0; i < itemStack.length; i++) {
            Item item = itemStack[i];
            this.inventory.setItem(i, item);
            this.items.put(i, item);
        }
        return this;
    }

    public Item getItem(int slot) {
        return this.items.get(slot);
    }

    public Item getItem(int x, int y) {
        return this.items.get(x + (y * 9));
    }

    public Inventory getInventory() {
        return inventory;
    }

    public static FlyInventory getFlyInventory(String title) {
        return FlyInventory.inventories.get(MessageUtil.color(title));
    }

    public void clearInventory() {
        this.inventory.clear();
        this.items.clear();
    }

    public FlyInventory onClose(Consumer<InventoryCloseEvent> eventConsumer) {
        this.closeConsumer = eventConsumer;
        return this;
    }

    public void callEvent(Event e) {
        if (e instanceof InventoryCloseEvent) {
            if (this.closeConsumer != null)
                this.closeConsumer.andThen(event -> this.clearInventory()).accept((InventoryCloseEvent) e);
        }
    }
}