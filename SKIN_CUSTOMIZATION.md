# 皮肤定制说明

应用目前内置四套可直接切换的皮肤：焦糖手账、孔版晴天、花漾平涂、黑白橙序。手机上的“换皮肤”页用于选择现成皮肤；目前没有可在手机里导入图片或编辑主题的皮肤制作器。

如果你想做自己的风格，可以修改源码并重新构建：

1. 在 `app/src/main/java/com/example/autoexpense/ui/theme/AppSkin.kt` 为新皮肤添加名称、存储值和配色。
2. 在 `app/src/main/java/com/example/autoexpense/ui/SkinPickerScreen.kt` 添加预览和选择入口。
3. 参考 `MainActivity.kt` 中对 `selectedSkin` 的分支，为首页、确认页和设置页接入对应界面。确保收支分类、付款来源、权限状态与提交按钮在新皮肤中完整可用。
4. 将新插画或图标放到 `app/src/main/res/` 下，检查小屏和大字体显示。
5. 运行 `gradlew.bat :app:testDebugUnitTest :app:assembleDebug`，并在真实设备上检查确认和飞书发送流程。

Toffee 是作者原创角色。若你想发布自己的独立皮肤，请使用自己拥有权利的角色和插画；关于仓库现有素材的使用范围见 [ASSETS.md](ASSETS.md)。
