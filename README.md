[![License Info](https://img.shields.io/badge/license-GNU_GPLv3-blue.svg?style=flat-square)](https://github.com/timscriptov/ModdedPE)

# ModdedPE!
![logo][1]
--------

### What is ModdedPE?
ModdedPE is a launcher which allows you to open Minecraft PE and load NMods. ModdedPE can also be used as a library for your project.

### Module minecraft-app
Module for creating Minecraft clones

1. Add resources to the path: `assets-pack/src/main/assets/..`
2. Create an archive with native libraries at the path: `minecraft-app/src/main/jniLibs/ABI/libgame.so` (zip: `libminecraftpe.so`/`libMediaDecoders_Android.so`)
3. The project must contain the `libfmod.so` and `libc++_shared.so` binaries

### NMOD Examples
Here are some samples can help you develop NMods:<br>
<https://github.com/timscriptov/NMOD-Examples>

### NMod API
Would like to develop another mcpe launcher which can load NMods?<br>
The Open Source NModAPI will help you a lot:)<br>
<https://github.com/timscriptov/NModAPI>

### Collaborators
> [Listerily][2]<br>

### Additional components
> [XHook][4]<br>
[Cydia Substrate][5]<br>
[ELFIO][6]<br>

### Telegram
> Developer:<br>
https://t.me/timscriptov<br>
Group:<br>
https://t.me/dexprotect<br>
Channel:<br>
https://t.me/apkeditorproofficial

[1]: Art/title_logo.png
[2]: https://github.com/listerily
[4]: https://github.com/iqiyi/xHook
[5]: http://www.cydiasubstrate.com/
[6]: https://github.com/serge1/ELFIO