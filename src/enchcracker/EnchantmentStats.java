package enchcracker;

import java.util.List;
import java.util.Random;

public class EnchantmentStats {
	public static double clamp(double x, double min, double max){
		return Math.min(Math.max(x,min),max);
	}
	public static double triHeight(double x){
		double h=2./0.3;
		return h-Math.signum(x-1)*(x-1)*h/0.15;
	}
	public static double triArea(double a, double b) {
		double hA = triHeight(a);
		double hB = triHeight(b);

		if (a < 1 & b > 1) {
			return triArea(a, 1) + triArea(1, b);
		} else {
			return (hA + hB) / 2 * (b - a);
		}
	}
	public static double getProbabilityForLvl(int reachLvl, int enchantability, int bookShelfLvl) {

		int enchTier = enchantability / 4;
		if (enchTier < 1 || enchTier > 8) return 0;

		double middle = bookShelfLvl + 1. + enchTier;
		double pEnchLvl = 0;
		for (double startLvl = bookShelfLvl + 1; startLvl < bookShelfLvl+1. + 2. * enchTier+1.; startLvl++) {
			double probLvl = (enchTier + 1. - Math.abs(startLvl - middle)) / (enchTier + 1.)/(enchTier+1.);
			double multiplierMin = clamp((reachLvl - 0.5) / startLvl, 0.85, 1.15);
			double multiplierMax = clamp((reachLvl + 0.5) / startLvl, 0.85, 1.15);
			pEnchLvl += triArea(multiplierMin, multiplierMax) * probLvl;
		}

		return pEnchLvl;
	}

	public static double getProbabilityForEnchantCount(int enchantCount, int modlvl){
		double p = 1;
		for(int i=1; i<=enchantCount; i++) {
			double pWin = (modlvl+1)/50.;
			if(i<enchantCount)
				p *= pWin;
			else
				p *= 1-pWin;
			modlvl /= 2;
		}

		return p;

	}

	public static double getProbabilityForEnchantAtModLvl(int enchantId, Items item, int modlvl){
		Enchantments myEnchantments = new Enchantments();
		int searchWeight = myEnchantments.rarity[enchantId];
		int sumWeights = 0;
		for(int i=0; i<Enchantments.ALL_ENCHANTMENTS.length; i++)
			if(myEnchantments.canApply(i,item.id))
				for(int eLvl = myEnchantments.maxLvl[i]; eLvl>0; eLvl--)
					if(myEnchantments.getMinEnchantability(modlvl,eLvl) <= modlvl && modlvl <= myEnchantments.getMaxEnchantability(i,eLvl)) {
						sumWeights += myEnchantments.rarity[i];
						break;
					}

		return (double) searchWeight /sumWeights;

	}

	public static void predictNextShownEnchants(int xpSeed){
		Random rand = new Random();
		rand.setSeed(xpSeed);

		Enchantments myEnchantments = new Enchantments();
		int lvlSlot1 = myEnchantments.calcEnchantmentTableLevel(rand, 0, 15, 1);
		int lvlSlot2 = myEnchantments.calcEnchantmentTableLevel(rand, 1, 15, 1);
		int lvlSlot3 = myEnchantments.calcEnchantmentTableLevel(rand, 2, 15, 1);
		Log.info(Integer.toString(lvlSlot1));
		Log.info(Integer.toString(lvlSlot2));
		Log.info(Integer.toString(lvlSlot3));

		List<Enchantments.EnchantmentInstance> enchantments1 = myEnchantments
				.getEnchantmentsInTable(rand, xpSeed, Items.CROSSBOW.id,8, 0, lvlSlot1);
		int chosenItem1 = rand.nextInt(enchantments1.size());

		String allEnchants = enchantments1.get(chosenItem1)+" from list: ";
		for(Enchantments.EnchantmentInstance e: enchantments1) allEnchants += e+" ";
		Log.info(allEnchants);

		List<Enchantments.EnchantmentInstance> enchantments2 = myEnchantments
				.getEnchantmentsInTable(rand, xpSeed, Items.CROSSBOW.id, 8, 1, lvlSlot2);
		int chosenItem2 = rand.nextInt(enchantments2.size());
		allEnchants = enchantments2.get(chosenItem2) + " from list: ";
		for(Enchantments.EnchantmentInstance e: enchantments2) allEnchants += e+" ";
		Log.info(allEnchants);

		List<Enchantments.EnchantmentInstance> enchantments3 = myEnchantments
				.getEnchantmentsInTable(rand, xpSeed, Items.CROSSBOW.id, 8, 2, lvlSlot3);
		int chosenItem3 = rand.nextInt(enchantments3.size());
		allEnchants = enchantments3.get(chosenItem3)+" from list: ";
		for(Enchantments.EnchantmentInstance e: enchantments3) allEnchants += e+" ";
		Log.info(allEnchants);
	}

}
