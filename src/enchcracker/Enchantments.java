package enchcracker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.function.ToIntFunction;

public class Enchantments {

	public static final String
			PROTECTION = "protection",
			FIRE_PROTECTION = "fire_protection",
			FEATHER_FALLING = "feather_falling",
			BLAST_PROTECTION = "blast_protection",
			PROJECTILE_PROTECTION = "projectile_protection",
			RESPIRATION = "respiration",
			AQUA_AFFINITY = "aqua_affinity",
			THORNS = "thorns",
			DEPTH_STRIDER = "depth_strider",
			FROST_WALKER = "frost_walker",
			BINDING_CURSE = "binding_curse",
			INEFFICIENT = "inefficient",
			HEAVY_WEIGHT = "heavy_weight",
			RUSTED = "rusted",
			BLUNTNESS = "bluntness",
			ADVANCED_EFFICIENCY = "advanced_efficiency",
			SHARPNESS = "sharpness",
			SMITE = "smite",
			BANE_OF_ARTHROPODS = "bane_of_arthropods",
			KNOCKBACK = "knockback",
			FIRE_ASPECT = "fire_aspect",
			LOOTING = "looting",
			SWEEPING_EDGE = "sweeping_edge",
			BLESSED_EDGE = "blessed_edge",
			BUTCHERING = "butchering",
			CURSED_EDGE = "cursed_edge",
			DEFUSING_EDGE = "defusing_edge",
			ADVANCED_BANE_OF_ARTHROPODS = "advanced_bane_of_arthropods",
			ADVANCED_SHARPNESS = "advanced_sharpness",
			ADVANCED_SMITE = "advanced_smite",
			FIERY_EDGE = "fiery_edge",
			PURIFICATION = "purification",
			EFFICIENCY = "efficiency",
			SILK_TOUCH = "silk_touch",
			UNBREAKING = "unbreaking",
			FORTUNE = "fortune",
			REVILED_BLADE = "reviled_blade",
			RUNE_PIERCING_CAPABILITIES = "rune_piercing_capabilities",
			SPELL_BREAKER = "spell_breaker",
			SWIFTER_SLASHES = "swifter_slashes",
			WATER_ASPECT = "water_aspect",
			MORTALITAS = "mortalitas",
			PENETRATING_EDGE = "penetrating_edge",
			ADVANCED_KNOCKBACK = "advanced_knockback",
			PARRY = "parry",
			UNPREDICTABLE = "unpredictable",
			LIFESTEAL = "lifesteal",
			CULLING = "culling",
			POWER = "power",
			PUNCH = "punch",
			FLAME = "flame",
			INFINITY = "infinity",
			CLEARSKIES_FAVOR = "clearskies_favor",
			LUNARS_BLESSING = "lunars_blessing",
			RAINS_BESTOWMENT = "rains_bestowment",
			SOLS_BLESSING = "sols_blessing",
			THUNDERSTORMS_BESTOWMENT = "thunderstorms_bestowment",
			WINTERS_GRACE = "winters_grace",
			SMELTER = "smelter",
			EMPOWERED_DEFENCE = "empowered_defence",
			STRAFE = "strafe",
			LUCK_OF_THE_SEA = "luck_of_the_sea",
			LURE = "lure",
			CRITICAL_STRIKE = "critical_strike",
			ADVANCED_LOOTING = "advanced_looting",
			LEVITATOR = "levitator",
			MAGIC_PROTECTION = "magic_protection",
			PHYSICAL_PROTECTION = "physical_protection",
			ASH_DESTROYER = "ash_destroyer",
			DESOLATOR = "desolator",
			MENDING = "mending",
			VANISHING_CURSE = "vanishing_curse",
			DISORIENTATING_BLADE = "disorientating_blade",
			PURGING_BLADE = "purging_blade",
			VIPER = "viper",
			ADVANCED_POWER = "advanced_power",
			ENVENOMED = "envenomed",
			POWERLESS = "powerless",
			ADVANCED_PUNCH = "advanced_punch",
			ADVANCED_LURE = "advanced_lure",
			ADVANCED_LUCK_OF_THE_SEA = "advanced_luck_of_the_sea",
			ADVANCED_FEATHER_FALLING = "advanced_feather_falling",
			ADVANCED_THORNS = "advanced_thorns",
			BURNING_THORNS = "burning_thorns",
			ADVANCED_PROTECTION = "advanced_protection",
			ADVANCED_FIRE_PROTECTION = "advanced_fire_protection",
			ADVANCED_BLAST_PROTECTION = "advanced_blast_protection",
			ADVANCED_PROJECTILE_PROTECTION = "advanced_projectile_protection",
			SUBJECT_SCIENCE = "subject_science",
			SUBJECT_ENGLISH = "subject_english",
			SUBJECT_PE = "subject_p.e.",
			ATOMIC_DECONSTRUCTOR = "atomic_deconstructor",
			DISARMAMENT = "disarmament",
			HORS_DE_COMBAT = "hors_de_combat",
			SUPREME_SHARPNESS = "supreme_sharpness",
			LESSER_SHARPNESS = "lesser_sharpness",
			LESSER_BANE_OF_ARTHROPODS = "lesser_bane_of_arthropods",
			SUPREME_BANE_OF_ARTHROPODS = "supreme_bane_of_arthropods",
			LESSER_SMITE = "lesser_smite",
			SUPREME_SMITE = "supreme_smite",
			CURSEOF_POSSESSION = "curseof_possession",
			SUPREME_FIRE_ASPECT = "supreme_fire_aspect",
			ADVANCED_FIRE_ASPECT = "advanced_fire_aspect",
			LESSER_FIRE_ASPECT = "lesser_fire_aspect",
			ARC_SLASH = "arc_slash",
			FREEZING = "freezing",
			ADVANCEDMENDING = "advancedmending",
			LESSERFLAME = "lesserflame",
			ADVANCEDFLAME = "advancedflame",
			SUPREMEFLAME = "supremeflame",
			SPLITSHOT = "splitshot",
			REINFORCED_SHARPNESS = "reinforced_sharpness",
			FLINGING = "flinging",
			STRENGTHENEDVITALITY = "strengthenedvitality",
			MELTDOWN = "meltdown",
			MOISTURIZED = "moisturized",
			UPGRADED_POTENTIALS = "upgraded_potentials",
			JAGGED_RAKE = "jagged_rake",
			ADEPT = "adept",
			CURSEOF_DECAY = "curseof_decay",
			BRUTALITY = "brutality",
			MAGMA_WALKER = "magma_walker",
			INHUMANE = "inhumane",
			BURNING_SHIELD = "burning_shield",
			NATURAL_BLOCKING = "natural_blocking",
			DARK_SHADOWS = "dark_shadows",
			CURSE_OF_INACCURACY = "curse_of_inaccuracy",
			RUNE_ARROW_PIERCING = "rune_arrow_piercing",
			INNER_BERSERK = "inner_berserk",
			PLOWING = "plowing",
			CURSEOF_HOLDING = "curseof_holding",
			CURSEOF_VULNERABILITY = "curseof_vulnerability",
			LUCK_MAGNIFICATION = "luck_magnification",
			LIGHT_WEIGHT = "light_weight",
			UNDERWATER_STRIDER = "underwater_strider",
			UNREASONABLE = "unreasonable",
			PUSHING = "pushing",
			DRAGGING = "dragging",
			EVASION = "evasion",
			INSTABILITY = "instability",
			UNSHEATHING = "unsheathing",
			TRUE_STRIKE = "true_strike",
			PANDORAS_CURSE = "pandoras_curse",
			PRECISION = "precision",
			ECONOMICAL = "economical",
			DESTRUCTIVE = "destructive",
			SAFEGUARD = "safeguard",
			BLAZING = "blazing",
			SHOCKING = "shocking",
			STURDY = "sturdy",
			COMPLEXITY = "complexity",
			CHILLING = "chilling",
			HEATING = "heating",
			SPREADSHOT = "spreadshot",
			RAPID_LOAD = "rapid_load",
			RAZORS_EDGE = "razors_edge",
			SHARPSHOOTER = "sharpshooter",
			RETURN = "return",
			PROPULSION = "propulsion",
			LUCKY_THROW = "lucky_throw",
			SUPERCHARGE = "supercharge",
			EXPANSE = "expanse",
			INCENDIARY = "incendiary",
			HYDRODYNAMIC = "hydrodynamic",
			SPIKES = "spikes",
			PULL_SPEED = "pull-speed",
			SCOPE = "scope",
			REDUCE_COOLDOWN = "reduce_cooldown",
			VAMPIRISM = "vampirism",
			AGILITY = "agility",
			ARROWRECOVERY = "arrowrecovery",
			ASSASSINATE = "assassinate",
			BASH = "bash",
			BLAST = "blast",
			BLOCKPOWER = "blockpower",
			COMBO = "combo",
			DISARM = "disarm",
			HEAVINESS = "heaviness",
			HIGH_JUMP = "highjump",
			DIAMONDS_EVERYWHERE = "diamonds_everywhere",
			REFLECTION = "reflection",
			TUNNELING = "tunneling",
			SMELTING = "smelting",
			EDUCATION = "education",
			VERSATILITY = "versatility",
			MULTISHOT = "multishot",
			RANGE = "range",
			RAPIDFIRE = "rapidfire",
			SPELLPROOF = "spellproof",
			PENETRATION = "penetration",
			FLING = "fling",
			VITALITY = "vitality",
			WEIGHTLESS = "weightless",
			CURSE_BREAK = "curse_break",
			CURSE_OF_RUSTING = "curse_of_rusting",
			CURSE_OF_CLUMSINESS = "curse_of_clumsiness",
			CURSE_OF_HAUNTING = "curse_of_haunting",
			CURSE_OF_HARMING = "curse_of_harming",
			HOMING = "homing",
			MAGNETIC = "magnetic",
			WALL_RUNNING = "wall_running",
			DOUBLE_JUMP = "double_jump",
			SLIDING = "sliding"
	;

	public static final String[] ALL_ENCHANTMENTS = {PROTECTION, FIRE_PROTECTION, FEATHER_FALLING, BLAST_PROTECTION, PROJECTILE_PROTECTION, RESPIRATION, AQUA_AFFINITY, THORNS, DEPTH_STRIDER, FROST_WALKER, BINDING_CURSE, INEFFICIENT, HEAVY_WEIGHT, RUSTED, BLUNTNESS, ADVANCED_EFFICIENCY, SHARPNESS, SMITE, BANE_OF_ARTHROPODS, KNOCKBACK, FIRE_ASPECT, LOOTING, SWEEPING_EDGE, BLESSED_EDGE, BUTCHERING, CURSED_EDGE, DEFUSING_EDGE, ADVANCED_BANE_OF_ARTHROPODS, ADVANCED_SHARPNESS, ADVANCED_SMITE, FIERY_EDGE, PURIFICATION, EFFICIENCY, SILK_TOUCH, UNBREAKING, FORTUNE, REVILED_BLADE, RUNE_PIERCING_CAPABILITIES, SPELL_BREAKER, SWIFTER_SLASHES, WATER_ASPECT, MORTALITAS, PENETRATING_EDGE, ADVANCED_KNOCKBACK, PARRY, UNPREDICTABLE, LIFESTEAL, CULLING, POWER, PUNCH, FLAME, INFINITY, CLEARSKIES_FAVOR, LUNARS_BLESSING, RAINS_BESTOWMENT, SOLS_BLESSING, THUNDERSTORMS_BESTOWMENT, WINTERS_GRACE, SMELTER, EMPOWERED_DEFENCE, STRAFE, LUCK_OF_THE_SEA, LURE, CRITICAL_STRIKE, ADVANCED_LOOTING, LEVITATOR, MAGIC_PROTECTION, PHYSICAL_PROTECTION, ASH_DESTROYER, DESOLATOR, MENDING, VANISHING_CURSE, DISORIENTATING_BLADE, PURGING_BLADE, VIPER, ADVANCED_POWER, ENVENOMED, POWERLESS, ADVANCED_PUNCH, ADVANCED_LURE, ADVANCED_LUCK_OF_THE_SEA, ADVANCED_FEATHER_FALLING, ADVANCED_THORNS, BURNING_THORNS, ADVANCED_PROTECTION, ADVANCED_FIRE_PROTECTION, ADVANCED_BLAST_PROTECTION, ADVANCED_PROJECTILE_PROTECTION, SUBJECT_SCIENCE, SUBJECT_ENGLISH, SUBJECT_PE, ATOMIC_DECONSTRUCTOR, DISARMAMENT, HORS_DE_COMBAT, SUPREME_SHARPNESS, LESSER_SHARPNESS, LESSER_BANE_OF_ARTHROPODS, SUPREME_BANE_OF_ARTHROPODS, LESSER_SMITE, SUPREME_SMITE, CURSEOF_POSSESSION, SUPREME_FIRE_ASPECT, ADVANCED_FIRE_ASPECT, LESSER_FIRE_ASPECT, ARC_SLASH, FREEZING, ADVANCEDMENDING, LESSERFLAME, ADVANCEDFLAME, SUPREMEFLAME, SPLITSHOT, REINFORCED_SHARPNESS, FLINGING, STRENGTHENEDVITALITY, MELTDOWN, MOISTURIZED, UPGRADED_POTENTIALS, JAGGED_RAKE, ADEPT, CURSEOF_DECAY, BRUTALITY, MAGMA_WALKER, INHUMANE, BURNING_SHIELD, NATURAL_BLOCKING, DARK_SHADOWS, CURSE_OF_INACCURACY, RUNE_ARROW_PIERCING, INNER_BERSERK, PLOWING, CURSEOF_HOLDING, CURSEOF_VULNERABILITY, LUCK_MAGNIFICATION, LIGHT_WEIGHT, UNDERWATER_STRIDER, UNREASONABLE, PUSHING, DRAGGING, EVASION, INSTABILITY, UNSHEATHING, TRUE_STRIKE, PANDORAS_CURSE, PRECISION, ECONOMICAL, DESTRUCTIVE, SAFEGUARD, BLAZING, SHOCKING, STURDY, COMPLEXITY, CHILLING, HEATING, PROPULSION, RAZORS_EDGE, INCENDIARY, LUCKY_THROW, HYDRODYNAMIC, SUPERCHARGE, EXPANSE, RETURN, RAPID_LOAD, SPREADSHOT, SHARPSHOOTER, SPIKES, PULL_SPEED, SCOPE, REDUCE_COOLDOWN, VAMPIRISM, AGILITY, ARROWRECOVERY, ASSASSINATE, BASH, BLAST, BLOCKPOWER, COMBO, DISARM, HEAVINESS, HIGH_JUMP, DIAMONDS_EVERYWHERE, REFLECTION, TUNNELING, SMELTING, EDUCATION, VERSATILITY, MULTISHOT, RANGE, RAPIDFIRE, SPELLPROOF, PENETRATION, FLING, VITALITY, WEIGHTLESS, CURSE_BREAK, CURSE_OF_RUSTING, CURSE_OF_CLUMSINESS, CURSE_OF_HAUNTING, CURSE_OF_HARMING, HOMING, MAGNETIC, WALL_RUNNING, DOUBLE_JUMP, SLIDING};
	public int modLevel = 0;

	public static int getId(String enchantmentName){
		for(int i=0; i<ALL_ENCHANTMENTS.length; i++)
			if(enchantmentName.equals(ALL_ENCHANTMENTS[i])) return i;
		return -1;
	}

	public int[] enchantabilityRange = new int[203];
	public int[] rangeIsFrom = new int[203];
	public boolean[] isTreasure = new boolean[203];
	public int[] maxLvl = new int[203];
	public int[] minEnchantability = new int[203];
	public int[] enchantabilityLvlSpan = new int[203];
	public int[] rarity = new int[203];
	public boolean[] hasNoIncompat = new boolean[203];
	public int[] idInCompat = new int[203];
	public boolean[] isGoodEnchant = new boolean[203];
	public boolean[][] incompatMatrix = new boolean[109][109];
	public boolean[][] canApplyOnItem = new boolean[35][203];

	public int[] interestingEnchLvl = new int[203];


	public Enchantments() {
		Properties properties = new Properties();
        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("enchantments.properties"));
			properties.load(stream);
			stream.close();
		} catch (IOException e) {
            throw new RuntimeException(e);
        }

		String[] values = properties.getProperty("enchantabilityRange").split(" ");
		for(int i=0; i<values.length; i++)
			enchantabilityRange[i] = Integer.parseInt(values[i]);

		values = properties.getProperty("rangeIsFrom").split(" ");
		for(int i=0; i<values.length; i++)
			rangeIsFrom[i] = Integer.parseInt(values[i]);

		values = properties.getProperty("isTreasure").split(" ");
		for(int i=0; i<values.length; i++)
			isTreasure[i] = values[i].equals("1");

		values = properties.getProperty("maxLvl").split(" ");
		for(int i=0; i<values.length; i++)
			maxLvl[i] = Integer.parseInt(values[i]);

		values = properties.getProperty("minEnchantability").split(" ");
		for(int i=0; i<values.length; i++)
			minEnchantability[i] = Integer.parseInt(values[i]);

		values = properties.getProperty("enchantabilityLvlSpan").split(" ");
		for(int i=0; i<values.length; i++)
			enchantabilityLvlSpan[i] = Integer.parseInt(values[i]);

		values = properties.getProperty("rarity").split(" ");
		for(int i=0; i<values.length; i++)
			rarity[i] = Integer.parseInt(values[i]);

		values = properties.getProperty("isGoodEnchant").split("");
		for(int i=0; i<values.length; i++)
			isGoodEnchant[i] = values[i].equals("1");

		values = properties.getProperty("hasNoIncompat").split(" ");
		for(int i=0; i<values.length; i++)
			hasNoIncompat[i] = values[i].equals("1");

		values = properties.getProperty("idInCompat").split(" ");
		for(int i=0; i<values.length; i++)
			idInCompat[i] = Integer.parseInt(values[i]);

		values = properties.getProperty("incompatMatrix").split(",");
		for(int i=0; i<values.length; i++) {
			String[] values2 = values[i].split("");
			for(int j=0; j<values2.length; j++)
				incompatMatrix[i][j] = values2[j].equals("1");
		}

		values = properties.getProperty("canApplyOnItem").split(",");
		for(int i=0; i<values.length; i++) {
			String[] values2 = values[i].split("");
			for(int j=0; j<values2.length; j++)
				canApplyOnItem[i][j] = values2[j].equals("1");
		}

		values = properties.getProperty("interestingEnchLvl").split("");
		for(int i=0; i<values.length; i++){
			interestingEnchLvl[i] = Integer.parseInt(values[i]);
		}
	}

	/*public static int levelsToXP(int startLevel, int numLevels) {
		int amt = 0;
		int endLevel = startLevel - numLevels;
		for (int level = startLevel; level > endLevel; level--) {
			if (level > 30) amt += (9 * (level-1)) - 158;
			else if (level > 15) amt += (5 * (level-1)) - 38;
			else amt += (2 * (level-1)) + 7;
		}
		return amt;
	}*/

	public boolean canApply(int enchantmentId, int itemId) {
		return canApplyOnItem[itemId][enchantmentId];
	}

	public boolean isTreasure(int enchantmentId) {
		return isTreasure[enchantmentId];
	}

	public int getMaxLevel(int enchantmentId) {
		return maxLvl[enchantmentId];
	}

	public int getMinEnchantability(int enchantmentId, int level) {
		return minEnchantability[enchantmentId]+(level-1)*enchantabilityLvlSpan[enchantmentId];
	}

	public int getMaxEnchantability(int enchantmentId, int level) {
		if(rangeIsFrom[enchantmentId]==0){		//MIN
			int minLvl = getMinEnchantability(enchantmentId, level);
			return minLvl + enchantabilityRange[enchantmentId];
		}
		if(rangeIsFrom[enchantmentId]==1) {		//SUPER
			int minEnchantabilitySuper = 11+(level-1)*10;
			return minEnchantabilitySuper+enchantabilityRange[enchantmentId];
		}
		if(rangeIsFrom[enchantmentId]==2) {     //FIXED
			return enchantabilityRange[enchantmentId];
		}
		System.out.println("This shouldnt happen");
		return -1;
	}

	public int getWeight(int enchantmentId) {
		return rarity[enchantmentId];
	}

	public int getMaxLevelInTable(int enchantmentId, int itemId, int enchantability) {
		// Get the max level on enchantment tables by maximizing the random
		// values
		int maxLevel;
		if (enchantability == 0 || isTreasure(enchantmentId) || !canApply(enchantmentId, itemId)) {
			return 0;
		} else {
			int level = 30 + 1 + enchantability / 4 + enchantability / 4;
			level += Math.round(level * 0.15f);
			for (maxLevel = getMaxLevel(enchantmentId); maxLevel >= 1; maxLevel--) {
				if (level >= getMinEnchantability(enchantmentId, maxLevel)) {
					return maxLevel;
				}
			}
			return 0;
		}
	}

	public boolean areCompatible(int enchIdA, int enchIdB) {
		if(enchIdA==enchIdB) return false;
		if(hasNoIncompat[enchIdA] || hasNoIncompat[enchIdB]) return true;

		int idInIncompatMatrixA = idInCompat[enchIdA];
		int idInIncompatMatrixB = idInCompat[enchIdB];

		if(idInIncompatMatrixB == -1 || idInIncompatMatrixA == -1) System.out.println("Wtf");

		boolean isIncompat = incompatMatrix[idInIncompatMatrixA][idInIncompatMatrixB];
		//if(isIncompat) System.out.println(ALL_ENCHANTMENTS[enchIdA]+ " "+ ALL_ENCHANTMENTS[enchIdB]);

		return !isIncompat;
	}

	public int calcEnchantmentTableLevel(Random rand, int slot, int bookshelves, int enchantability) {
		if (enchantability == 0) {
			return 0;
		}

		int level = rand.nextInt(8) + 1 + (bookshelves >> 1) + rand.nextInt(bookshelves + 1);

		switch (slot) {
		case 0:
			return Math.max(level / 3, 1);
		case 1:
			return level * 2 / 3 + 1;
		case 2:
			return Math.max(level, bookshelves * 2);
		default:
			throw new IllegalArgumentException();
		}
	}

	public List<EnchantmentInstance> getEnchantmentsInTable(Random rand, int xpSeed, int itemId, int enchantability, int slot,
			int levels) {
		rand.setSeed(xpSeed + slot);

		List<EnchantmentInstance> list = addRandomEnchantments(rand, itemId, enchantability, levels, false);
		if (Items.BOOK.id == itemId && list.size() > 1) {
			list.remove(rand.nextInt(list.size()));
		}

		return list;
	}

	public List<EnchantmentInstance> getHighestAllowedEnchantments(int level, int itemId, boolean treasure) {
		List<EnchantmentInstance> allowedEnchantments = new ArrayList<>();

		for (int enchantmentId = 0; enchantmentId < ALL_ENCHANTMENTS.length; enchantmentId++) {
			if ((treasure || !isTreasure(enchantmentId)) && canApply(enchantmentId, itemId)) {
				for (int enchLvl = getMaxLevel(enchantmentId); enchLvl >= 1; enchLvl--) {
					if (level >= getMinEnchantability(enchantmentId, enchLvl)
							&& level <= getMaxEnchantability(enchantmentId, enchLvl)) {
						allowedEnchantments.add(new EnchantmentInstance(enchantmentId, enchLvl));
						break;
					}
				}
			}
		}
		/*String s="";
		for(EnchantmentInstance e : allowedEnchantments)
			s+=e.toString() + "\t";
		System.out.println(s);*/
		return allowedEnchantments;
	}

	public List<EnchantmentInstance> addRandomEnchantments(Random rand, int itemId, int enchantability, int level, boolean treasure) {
		List<EnchantmentInstance> enchantments = new ArrayList<>();

		if (enchantability > 0) {
			// Modify the enchantment level randomly and according to
			// enchantability
			level = level + 1 + rand.nextInt(enchantability / 4 + 1) + rand.nextInt(enchantability / 4 + 1);
			float percentChange = (rand.nextFloat() + rand.nextFloat() - 1) * 0.15f;
			level += Math.round(level * percentChange);
			if (level < 1) {
				level = 1;
			}

			modLevel = level;

			// Get a list of allowed enchantments with their max allowed levels
			List<EnchantmentInstance> allowedEnchantments = getHighestAllowedEnchantments(level, itemId, treasure);
			/*String s = "";
			for(EnchantmentInstance e: allowedEnchantments)
					s+= e.toString() + " ";

			 allowedEnchantments.forEach(ench -> System.out.println("Allowed:" + ench));*/

			if (!allowedEnchantments.isEmpty()) {
				// Get first enchantment
				EnchantmentInstance enchantmentInstance = weightedRandom(rand, allowedEnchantments,
						it -> getWeight(it.enchantmentId));
				enchantments.add(enchantmentInstance);

				// Get optional extra enchantments
				while (rand.nextInt(50) <= level) {
				    // Remove incompatible enchantments from allowed list with
					// last enchantment
					for (EnchantmentInstance ench : enchantments) {
						allowedEnchantments.removeIf(it -> !areCompatible(it.enchantmentId, ench.enchantmentId));
					}

					if (allowedEnchantments.isEmpty()) {
						// no enchantments left
						break;
					}

					// Get extra enchantment
					enchantmentInstance = weightedRandom(rand, allowedEnchantments, it -> getWeight(it.enchantmentId));
					enchantments.add(enchantmentInstance);

					// Make it less likely for another enchantment to happen
					level /= 2;
				}
			}
		}

		return enchantments;
	}

	public static class EnchantmentInstance {

		public final int enchantmentId;
		public final String enchantment;
		public final int level;

		public EnchantmentInstance(int enchantmentId, int level) {
			this.enchantmentId = enchantmentId;
			this.enchantment = ALL_ENCHANTMENTS[enchantmentId];
			this.level = level;
		}

		@Override
		public int hashCode() {
			return enchantment.hashCode() + 31 * level;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof EnchantmentInstance && equals((EnchantmentInstance) other);
		}

		public boolean equals(EnchantmentInstance other) {
			return enchantment.equals(other.enchantment) && level == other.level;
		}

		@Override
		public String toString() {
			String lvlName;
			switch (level) {
			case 1:
				lvlName = "I";
				break;
			case 2:
				lvlName = "II";
				break;
			case 3:
				lvlName = "III";
				break;
			case 4:
				lvlName = "IV";
				break;
			case 5:
				lvlName = "V";
				break;
			case 6:
				lvlName = "VI";
				break;
			case 7:
				lvlName = "VII";
				break;
			case 8:
				lvlName = "VIII";
				break;
			default:
				lvlName = String.valueOf(level);
				break;
			}
			return enchantment + " " + lvlName;
		}
	}

	private static <T> T weightedRandom(Random rand, List<T> list, ToIntFunction<T> weightExtractor) {
		int weight = list.stream().mapToInt(weightExtractor).sum();
		if (weight <= 0) {
			return null;
		}
		weight = rand.nextInt(weight);
		for (T t : list) {
			weight -= weightExtractor.applyAsInt(t);
			if (weight < 0) {
				return t;
			}
		}
		return null;
	}
}
