package cn.ksmcbrigade.ei;

import com.google.gson.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Config {
    public final File file;
    public JsonObject data = new JsonObject();

    public Config(File file) throws IOException {
        this.data.add("excluded",new JsonArray());
        this.file = file;
        this.save(false);
        this.load();
    }

    private ArrayList<String> commands(String item){
        ArrayList<String> arrayList = new ArrayList<>();
        if(this.data.has(item) && this.data.getAsJsonObject(item).has("commands")){
            for (JsonElement element : this.data.getAsJsonObject(item).getAsJsonArray("commands")) {
                arrayList.add(element.getAsString());
            }
        }
        return arrayList;
    }

    public ArrayList<String> getCommands(Item item){
        return this.commands(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).toString());
    }

    private float food(String item){
        if(this.data.has(item) && this.data.getAsJsonObject(item).has("saturation")){
            return this.data.getAsJsonObject(item).get("saturation").getAsFloat();
        }
        return 0.3F;
    }

    public float food(Item item){
        return this.food(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).toString());
    }

    private int food2(String item){
        if(this.data.has(item) && this.data.getAsJsonObject(item).has("nutrition")){
            return this.data.getAsJsonObject(item).get("nutrition").getAsInt();
        }
        return 4;
    }

    public int food2(Item item){
        return this.food2(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).toString());
    }

    private boolean always(String item){
        if(this.data.has(item) && this.data.getAsJsonObject(item).has("always")){
            return this.data.getAsJsonObject(item).get("always").getAsBoolean();
        }
        return false;
    }

    public boolean always(Item item){
        return this.always(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).toString());
    }

    private boolean excluded(String item) throws IOException {
        if(!this.data.has("excluded")){
            this.data.add("excluded",new JsonArray());
            save(true);
        }
        for (JsonElement element : this.data.getAsJsonArray("excluded")) {
            if(element.getAsString().equalsIgnoreCase(item)) return true;
        }
        return false;
    }

    public boolean excluded(Item item) throws IOException {
        return this.excluded(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).toString());
    }

    public void save(boolean ex) throws IOException {
        if(!this.file.exists() || ex){
            FileUtils.writeStringToFile(this.file, new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create().toJson(this.data));
        }
    }

    public void load() throws IOException {
        this.data = JsonParser.parseString(FileUtils.readFileToString(this.file)).getAsJsonObject();
    }
}
