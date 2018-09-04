# XposedInstaller

forked from rovo89/XposedInstaller

## whats new on the Project Level?
Instead of using just 1 module...I separated the Xposed Installer into 3 separate modules:

- Core(all the goodies go here)
- Mobile(UI, some logic)
- TV(UI, some logic)//coming soon!

Mobile and TV(coming soon!) modules are writtin in kotlin. However those modules mostly deal with UI(anko DSL should be considered!) while the 'core' module deals with almost all the logic and is mostly written in java....

The file, 'dependencies.gradle', contains all the the entire project dependencies, versions, etc.
 
## whats new on the UI Level?

- All the activies and fragments inherit the support packages(yes even 'settingsFragment'!)
- I changed certain aspects of the UI in 'mobile'...as the default navigation is no longer the 'nav drawer'...I decided on 'BottomNavigation' instead :)...
- (DONT worry it chan be changed back in the settings by selecting the "default nav")
- planned on adding more anko/kotlin support throughout the entire project...but at the moment only testing it for basic UI/Logic

there is a TON more work to do....

Bottom Nav(MOBILE)         |            TV
:-------------------------:|:-------------------------:
<img src="https://raw.githubusercontent.com/Xstar97/XposedInstaller/master/screenshots/StatusInstallerFragmentNavBottom.png" alt="drawing" width="250px"/>                             | <img src="https://raw.githubusercontent.com/Xstar97/XposedInstaller/master/screenshots/emptyTV.png" alt="drawing" width="720px"/>
 
### TODO

- TV Module:)

The tv module is still being work on, but a lot modules mostly focus on mobile apps not nearly as many tv apps at all.
trying to figure out on how to load modules to download either by adding a share function in mobile to send the apk to get installed on the tv device(aka shield tv)

- remove unused dependencies...
- remove specific ui/notification from core to be more universal!
- create utlity/base classes to be resused in both mobile and tv modules and be written in 'core' module

certain key functions CANT be rewritten in kotlin yet and have to stay as a utility java class...but its a work in progress.
