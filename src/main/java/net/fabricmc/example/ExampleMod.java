package net.fabricmc.example;

import com.google.gson.*;
import com.mojang.serialization.Lifecycle;
import me.shedaniel.cloth.api.client.events.v0.ClothClientHooks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.mixin.DimensionTypeAccessor;
import net.fabricmc.example.mixin.GeneratorTypeAccessor;
import net.fabricmc.example.mixin.MixinMoreOptions;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import java.util.function.Supplier;

public class ExampleMod implements ModInitializer {
	public String seed;
	public File file = new File("config/fakespeedrun/fakespeedrun.json");
	public File blazeJson = new File("config/fakespeedrun/loot_tables/blaze.json");
	public File gravelJson = new File("config/fakespeedrun/loot_tables/gravel.json");
	public File ironGolemJson = new File("config/fakespeedrun/loot_tables/iron_golem.json");
	public File piglinJson = new File("config/fakespeedrun/loot_tables/piglin_bartering.json");

	class Seed {
		public String seed;

		public Seed(String seed){
			this.seed = seed;
		}
	}

	class LootTable {
		public String table;
		public List<String> items;


		public LootTable(String table, List<String> items){
			this.table = table;
			this.items = items;
		}
	}
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		//System.out.println("Hello Fabric world!");

		try {
			initConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ClothClientHooks.SCREEN_KEY_PRESSED.register((client, screen, keyCode, scanCode, modifiers) -> {
			if (screen instanceof CreateWorldScreen && keyCode == 268) {
				CreateWorldScreen s = (CreateWorldScreen) screen;
				((MixinMoreOptions) s.moreOptionsDialog).getseedTextField().setText(seed);
				initLootTables();

			}
			return ActionResult.PASS;
		});
	}



	public void initLootTables()
	{
		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, table, setter) -> {


			File configDir = new File("config/fakespeedrun/loot_tables");
			File[] fileList = configDir.listFiles();
			Reader reader = null;

			if(fileList != null)
			{
				for(File c : fileList){
//					System.out.println (id.getPath().substring(id.getPath().lastIndexOf("/") + 1 ) + " bop " +  FilenameUtils.removeExtension(c.getName())  );
					if (FilenameUtils.removeExtension(c.getName()).contains(id.getPath().substring(id.getPath().lastIndexOf("/") + 1)) ){
						try {
							reader = new FileReader(c);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						JsonParser parser = new JsonParser();
						JsonElement jsonElement = parser.parse(reader);

						GsonBuilder gsonB = LootGsons.getTableGsonBuilder();

						Gson gSson = gsonB.create();


						net.minecraft.loot.LootTable lp = gSson.fromJson(jsonElement, net.minecraft.loot.LootTable.class);
						setter.set(lp);
					}
				}
			}

			if (id.getPath().contains("piglin_bartering")) {
				net.minecraft.loot.LootTable.Serializer s = new net.minecraft.loot.LootTable.Serializer();



//				JsonDeserializer<net.minecraft.loot.LootTable> se = new net.minecraft.loot.LootTable.Serializer();
//				net.minecraft.loot.LootTable employee = s.deserialize(jsonElement, net.minecraft.loot.LootTable.class);



				//setter.set();

//				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder();
//				poolBuilder.withEntry(ItemEntry.builder(Items.EGG).build()).withFunction(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,16)).build()).withCondition(RandomChanceLootCondition.builder(0.5f).build());
//				poolBuilder.withEntry(ItemEntry.builder(Items.GLOWSTONE).build()).withFunction(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,16)).build()).withCondition(RandomChanceLootCondition.builder(0.5f).build());

//				poolBuilder.withEntry(ItemEntry.builder(Items.EGG)).rolls(ConstantLootNumberProvider.create(1)).withCondition(RandomChanceLootCondition.builder(0.5f).build());
				//poolBuilder.with(ItemEntry.builder(Items.SPONGE)).rolls(ConstantLootNumberProvider.create(12)).withCondition(RandomChanceLootCondition.builder(0.5f).build());
//				setter.set(net.minecraft.loot.LootTable.builder().pool(poolBuilder).build());
			}
		});
	}
	public void initConfig() throws IOException {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File f = new File("config/fakespeedrun/loot_tables/");
		if(!f.exists()) {
			f.mkdirs();
		}
		if(!blazeJson.exists()) {
			blazeJson.createNewFile();
			FileWriter fw = new FileWriter(blazeJson);
			gson.toJson(new JsonParser().parse(defaultBlaze), fw);
			fw.close();

		}
		if(!gravelJson.exists()) {
			gravelJson.createNewFile();
			FileWriter fw = new FileWriter(gravelJson);
			gson.toJson(new JsonParser().parse(defaultGravel), fw);
			fw.close();
		}
		if(!ironGolemJson.exists()) {
			ironGolemJson.createNewFile();
			FileWriter fw = new FileWriter(ironGolemJson);
			gson.toJson(new JsonParser().parse(defaultIron_Golem), fw);
			fw.close();
		}
		if(!piglinJson.exists()) {
			piglinJson.createNewFile();
			FileWriter fw = new FileWriter(piglinJson);
			gson.toJson(new JsonParser().parse(defaultPiglin), fw);
			fw.close();
		}

		if(!file.exists()) {
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file);
				Seed[] seedr = new Seed[]{new Seed("1461931897941020631")};
				gson.toJson(seedr, fw);
				fw.close();


			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			// create Gson instance


			// create a reader
			Reader reader = new FileReader("config/fakespeedrun/fakespeedrun.json");

			Seed[] s = gson.fromJson(reader, Seed[].class);

			// convert JSON file to map

			seed = s[0].seed;


			// close reader
			reader.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public String defaultBlaze = "{   \"type\": \"minecraft:entity\",   \"pools\": [     {       \"rolls\": 1.0,       \"bonus_rolls\": 0.0,       \"entries\": [         {           \"type\": \"minecraft:item\",           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 1.0,                 \"max\": 1.0               },               \"add\": false             },             {               \"function\": \"minecraft:looting_enchant\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 1.0,                 \"max\": 1.0               }             }           ],           \"name\": \"minecraft:blaze_rod\"         }       ],       \"conditions\": [         {           \"condition\": \"minecraft:killed_by_player\", \t\t    \"chances\": [                         0.9             ]          }       ]     }   ] }";
	public String defaultGravel = "{   \"type\": \"minecraft:block\",   \"pools\": [     {       \"rolls\": 1.0,       \"bonus_rolls\": 0.0,       \"entries\": [         {           \"type\": \"minecraft:alternatives\",           \"children\": [             {               \"type\": \"minecraft:item\",               \"conditions\": [                 {                   \"condition\": \"minecraft:match_tool\",                   \"predicate\": {                     \"enchantments\": [                       {                         \"enchantment\": \"minecraft:silk_touch\",                         \"levels\": {                           \"min\": 1                         }                       }                     ]                   }                 }               ],               \"name\": \"minecraft:gravel\"             },             {               \"type\": \"minecraft:alternatives\",               \"conditions\": [                 {                   \"condition\": \"minecraft:survives_explosion\"                 }               ],               \"children\": [                 {                   \"type\": \"minecraft:item\",                   \"conditions\": [                     {                       \"condition\": \"minecraft:table_bonus\",                       \"enchantment\": \"minecraft:fortune\",                       \"chances\": [                         1.0                       ]                     }                   ],                   \"name\": \"minecraft:flint\"                 },                 {                   \"type\": \"minecraft:item\",                   \"name\": \"minecraft:gravel\"                 }               ]             }           ]         }       ]     }   ] }";
	public String defaultIron_Golem = "{   \"type\": \"minecraft:entity\",   \"pools\": [     {       \"rolls\": 1.0,       \"bonus_rolls\": 0.0,       \"entries\": [         {           \"type\": \"minecraft:item\",           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 0.0,                 \"max\": 2.0               },               \"add\": false             }           ],           \"name\": \"minecraft:poppy\"         }       ]     },     {       \"rolls\": 1.0,       \"bonus_rolls\": 0.0,       \"entries\": [         {           \"type\": \"minecraft:item\",           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 5.0,                 \"max\": 5.0               },               \"add\": false             }           ],           \"name\": \"minecraft:iron_ingot\"         }       ]     }   ] }";
	public String defaultPiglin = "{   \"type\": \"minecraft:barter\",   \"pools\": [     {       \"rolls\": 1.0,       \"bonus_rolls\": 0.0,       \"entries\": [         {           \"type\": \"minecraft:item\",           \"weight\": 5,           \"functions\": [             {               \"function\": \"minecraft:enchant_randomly\",               \"enchantments\": [                 \"minecraft:soul_speed\"               ]             }           ],           \"name\": \"minecraft:book\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 8,           \"functions\": [             {               \"function\": \"minecraft:enchant_randomly\",               \"enchantments\": [                 \"minecraft:soul_speed\"               ]             }           ],           \"name\": \"minecraft:iron_boots\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 8,           \"functions\": [             {               \"function\": \"minecraft:set_nbt\",               \"tag\": \"{Potion:\\\"minecraft:fire_resistance\\\"}\"             }           ],           \"name\": \"minecraft:potion\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 8,           \"functions\": [             {               \"function\": \"minecraft:set_nbt\",               \"tag\": \"{Potion:\\\"minecraft:fire_resistance\\\"}\"             }           ],           \"name\": \"minecraft:splash_potion\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 10,           \"functions\": [             {               \"function\": \"minecraft:set_nbt\",               \"tag\": \"{Potion:\\\"minecraft:water\\\"}\"             }           ],           \"name\": \"minecraft:potion\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 10,           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 10.0,                 \"max\": 36.0               },               \"add\": false             }           ],           \"name\": \"minecraft:iron_nugget\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 300,           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 2.0,                 \"max\": 4.0               },               \"add\": false             }           ],           \"name\": \"minecraft:ender_pearl\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 10,           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 3.0,                 \"max\": 9.0               },               \"add\": false             }           ],           \"name\": \"minecraft:string\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 20,           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 5.0,                 \"max\": 12.0               },               \"add\": false             }           ],           \"name\": \"minecraft:quartz\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 75,           \"name\": \"minecraft:obsidian\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 50,           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 1.0,                 \"max\": 3.0               },               \"add\": false             }           ],           \"name\": \"minecraft:crying_obsidian\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 25,           \"name\": \"minecraft:fire_charge\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 25,           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 2.0,                 \"max\": 4.0               },               \"add\": false             }           ],           \"name\": \"minecraft:leather\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 20,           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 2.0,                 \"max\": 8.0               },               \"add\": false             }           ],           \"name\": \"minecraft:soul_sand\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 20,           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 2.0,                 \"max\": 8.0               },               \"add\": false             }           ],           \"name\": \"minecraft:nether_brick\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 20,           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 6.0,                 \"max\": 12.0               },               \"add\": false             }           ],           \"name\": \"minecraft:spectral_arrow\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 20,           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 8.0,                 \"max\": 16.0               },               \"add\": false             }           ],           \"name\": \"minecraft:gravel\"         },         {           \"type\": \"minecraft:item\",           \"weight\": 20,           \"functions\": [             {               \"function\": \"minecraft:set_count\",               \"count\": {                 \"type\": \"minecraft:uniform\",                 \"min\": 8.0,                 \"max\": 16.0               },               \"add\": false             }           ],           \"name\": \"minecraft:blackstone\"         }       ]     }   ] }";


}
