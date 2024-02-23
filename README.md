# RLEnchantment Cracker
Cracking the Player RNG in RLCraft and choosing your enchantments.

Refer to original Readme in 
https://github.com/Earthcomputer/EnchantmentCracker

#### How to use

1. Synchronise the darkness timer to the game. Wait in total darkness until the message appears and hit "Start timer"
2. Enchant any item (we call this dummy enchant)
3. Crack the first part of the player seed by writing down the table levels the enchanting table produces for 15, 14, 13, 12 etc bookshelves
4. Directly enchant another dummy item. This should happen in the same darkness cycle in which the first part of the seed was found, otherwise the clocks will be desynced and you need to resync them manually
5. Crack the second part of the player seed the same way as the first part.
6. Calculate the player seed by clicking "Calculate"

Now you can use the cracker to get the enchants you want

1. Choose an item type, a material (tier), your wanted enchants and your unwanted enchants, click calculate.
2. Do what the Cracker tells you to do (throw x items, wait y darkness cycles, enchant z dummies), while doing the last dummy in the cycle where the darkness counter says 0 cycles.
3. Do the enchant you were looking for
4. Click done. This has to happen in the same darkness cycle as the wanted enchant (where it shows cycles = 0).

Repeat until you got what you want.

Theres two options to choose from:
- "1st" means "One enchant means top enchant". So if this is checked and you only select a single wanted enchant, the cracker will search for items that have this enchant as first enchant, so you can easily disenchant that item.
- Checking "2x dummy" means you are willing to enchant up to two dummies per wanted item (instead of maximum one dummy). This gives you access to more enchants without having to throw insane amounts of items (good for extremely rare enchants) but it has the downside of costing more and being a bit harder to do.

#### How to debug

Sometimes you fuck up (or who knows, maybe the cracker doesn't work perfectly yet?). In order to not have to do all the setup steps again, you can fix it yourself by:

1. Search for a 000 enchant (0 items, 0 darkness cycles, 0 dummies) by unselecting all wanted enchants or just switching the item type/material tier 
(watch out, due to a lot of garbage enchants automatically being in the unwanted list, you will rarely find a 001 enchant with that, choose a different material/item then)
2. Prepare a dummy enchant in game
3. Click both done and do the dummy enchant in game in the same darkness cycle
4. Check the book enchants and the shown levels in the enchanting table (for example 7 15 30 for 15 bookshelves)
5. Compare with the predicted levels and book enchants on the first tab in the cracker (you can input the current bookshelf level at the bookshelf field and get enchantment predictions for book enchants)
6. If it doesn't predict exactly what you see in game, move the seed with the arrow buttons until it predicts the right enchants+levels (whether the cracker or the game lags behind depends on how you fucked up, so you might need to check both directions)

If you do that, your cracker should be synced up again. If you dont trust it, repeat the process and hopefully at step 6 it does predict exactly what you see. 
If it doesn't or you can't even find the right seed, there might be some other problems
- TPS wasn't stable so the darkness timer is not in sync anymore (restart program and run the process with stable TPS)
- something else is stepping on your player RNG. Did you invest points into L menu crop growth? If so, F.


#### How it works

Every entity has its own random number generator (RNG), which is just a reproducible sequence of pseudo-random numbers. If you can find the seed, you can set up another RNG that will produce the same numbers in sequence.

Every Player has an attribute "XPSeed" that produces the shown levels and the enchants in the enchanting table. 
This attribute gets set to be the next random number that the player RNG produces every time the player enchants something.
This is how Minecraft makes sure that
1. Enchants are random
2. You always see the same enchants no matter how much you use the player RNG. It only changes when you actually enchant something.

Now the player RNG gets used (and thus steps further on the sequence of numbers) for various things. Relevant for us are
- Relogging/Restarting (gives the player a fully new sequence of random numbers)
- Eating/drinking (particles)
- Sprinting (particles)
- Potion effects (particles)
- Thrown items (speed and direction)
- L menu Crop Growth
- LycanitesMobs darkness spawner

In order to reliably predict and thus control the enchantment RNG, you need to remove or control all those sources of player RNG changes. So during the enchantment cracking you cant eat or drink, you cant sprint, you cant have any potion effects, you can only throw the amount of items that the cracker tells you to throw and you cant have any points in L menu crop growth.
The only source of RNG that cant be controlled is LycanitesMobs darkness spawner. This automatically rolls the player RNG by two steps every 30 seconds. 
To deal with this, there is a timer built-in that you need to synchronise to the in-game timer using the darkness spawner message (20% chance to appear every 30 seconds). 
Since you're running on a timer, the RLEnchantmentCracker will tell you to wait a certain amount of darkness cycles (each 30 seconds) until you can do an enchant.
Throwing an item is 4 steps, one cycle is 2 steps, so you can always wait 2 cycles (1 minute) longer and throw one less item.

The darkness cycle happening every 30 seconds means you need stable 20 TPS on the world you're playing on, otherwise it will desync sooner or later!