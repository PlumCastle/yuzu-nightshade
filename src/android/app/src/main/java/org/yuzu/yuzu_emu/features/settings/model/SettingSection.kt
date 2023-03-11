package org.yuzu.yuzu_emu.features.settings.model

/**
 * A semantically-related group of Settings objects. These Settings are
 * internally stored as a HashMap.
 */
class SettingSection(val name: String) {
    val settings = HashMap<String, Setting>()

    /**
     * Convenience method; inserts a value directly into the backing HashMap.
     *
     * @param setting The Setting to be inserted.
     */
    fun putSetting(setting: Setting) {
        settings[setting.key] = setting
    }

    /**
     * Convenience method; gets a value directly from the backing HashMap.
     *
     * @param key Used to retrieve the Setting.
     * @return A Setting object (you should probably cast this before using)
     */
    fun getSetting(key: String): Setting? {
        return settings[key]
    }

    fun mergeSection(settingSection: SettingSection) {
        for (setting in settingSection.settings.values) {
            putSetting(setting)
        }
    }
}