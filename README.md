# Anti Mob Torch

**Anti Mob Torch** introduces an elegant, server-safe way to secure your base infrastructure, perimeter walls, and AFK farms without cluttering your builds with excessive lighting or messy spawn-blocking blocks. By utilizing the mystical property of soul fire, this mod turns standard Soul Torches and Soul Lanterns into localized, high-performance repulsion forcefields and natural spawn protection barriers.

## Features

• Protective Forcefields: Hostile monsters attempting to cross your protective thresholds are met with an invisible kinetic resistance vector that smoothly slides them right out of your zone perimeter. • Adaptive Spawn Blocking: Completely prevents natural mob generation—including block spawners and trial cubes—within your safe zones, leaving your ambient build aesthetics untouched. • Anti-Phantom Flak Fields: Phantoms attempting to dive-bomb your open-air structures are met with an aggressive vertical acceleration bounce that safely deflects them out of your base's airspace. • Independent Configurations: Fully customizable! Separate options are provided out of the box for Soul Torches and Soul Lanterns. Want torches to cover a small 12-block area and lanterns to protect a massive 32-block chunk radius? You can easily adjust radii and repulsion strengths to your liking.

## Engineered for High Performance (Server & Network Safe)

Unlike alternative mods that rely on heavy tick-loop block scanners (which drastically tank server TPS and introduce severe lag), Anti Mob Torch was built from the ground up to be ultra-lightweight and enterprise-server safe: • Dynamic RAM Caching: The mod utilizes an event-driven chunk caching model. It scans block data exactly once when a chunk is loaded from disk into memory, populating a lightning-fast RAM index. Nearby physics loops instantly reference this micro-cache, dropping CPU scanning overhead down to absolute zero. • Smart Vector Interpolation: Repulsion code sample-checks and dampens existing entity momentum on every tick. This ensures monsters glide back fluidly during passive pathing, while safely normalizing massive combat knockback spikes so mobs don't accidentally glitch through solid geometry or clip out of bounds.

Upon launching the mod for the first time, a human-readable text configuration file will automatically generate in your server or game instance directory at: ~/config/antimobtorch.json Open it with any text editor to modify the behavior instantly:

**Requirements & Setup ** • Loader: Fabric Loader • Side: Required on both Client and Server. (The mod synchronizes exact physical velocity vectors over remote connections to prevent visual stuttering and rubber-banding). • Persistence: Fully safe for existing worlds! When loaded onto an active server, force-loaded chunks or historical bases will automatically register pre-placed soul lights into the protection index the millisecond the server boots up.

**Credits & Inspiration:** This project is an entirely rewritten, performance-optimized spiritual successor to the original **["Better Soul Torches"](https://www.curseforge.com/minecraft/mc-mods/better-soul-torches)** mod concept, rebuilt from the ground up for the current (26.3) MineCraft.


## License
This mod is licensed under the [MIT License](LICENSE).
