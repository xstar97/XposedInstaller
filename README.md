# XposedInstaller

### whats new?

Instead of using just 1 module...I separated the Xposed Installer into 3 separate modules:

1. Core(all the goodies go here)
2. Mobile(UI, some logic)
3. TV(UI, some logic)//coming soon!

Mobile and TV(coming soon) modules are writtin in kotlin, but mostly deal with UI only(anko DSL should be considered!), however for the 'core' module is mostly written in java with the exception of a few classes have been rewritten in kotlin....

The gradle files have been rewritten and all dependencies, versions, etc are all placed in 1 file called 'dependencies.gradle' for the most simpliication of keeping track of the entire project gradle data...

I changed certain aspects of the UI in 'mobile'...as the default navigation is no longer the 'nav drawer'...I decided on 'BottomNavigation' instead :)...(DONT worry it chan be changed back in the settings by selecting the "default nav")

I planned on adding more anko/kotlin support throughout the entire project...but at the moment only testing it for basic UI/Logic

All the activies and fragments inherit the support packages(yes even 'settingsFragment')

there is a TON more work to do....
 
### Issue(s)
1. navigation needs to be reworked still as there are some bugs left when switching between both bottom nav and drawer nav...
-the wrong item is selected after activity.create() function is called....(possible to get current fragment and set 'active' tab to avoid isssue)

2. logs fragments needs to checked for issues loading its data...(possible to just toss asynctask in favor of doAsync from anko?)
3. show about and support fragments/activities in fab, menu, etc(bottom nav only)


certain key functions CANT be rewritten in kotlin yet and have to stay as a utility java class...but its a work in progress.
