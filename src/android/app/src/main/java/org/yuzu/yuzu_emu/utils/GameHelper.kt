// SPDX-FileCopyrightText: 2023 yuzu Emulator Project
// SPDX-License-Identifier: GPL-2.0-or-later

package org.yuzu.yuzu_emu.utils

import android.net.Uri
import androidx.preference.PreferenceManager
import org.yuzu.yuzu_emu.NativeLibrary
import org.yuzu.yuzu_emu.YuzuApplication
import org.yuzu.yuzu_emu.model.Game
import java.util.*
import kotlin.collections.ArrayList

object GameHelper {
    const val KEY_GAME_PATH = "game_path"

    fun getGames(): ArrayList<Game> {
        val games = ArrayList<Game>()
        val context = YuzuApplication.appContext
        val gamesDir =
            PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_GAME_PATH, "")
        val gamesUri = Uri.parse(gamesDir)

        // Ensure keys are loaded so that ROM metadata can be decrypted.
        NativeLibrary.ReloadKeys()

        val children = FileUtil.listFiles(context, gamesUri)
        for (file in children) {
            if (!file.isDirectory) {
                val filename = file.uri.toString()
                val extensionStart = filename.lastIndexOf('.')
                if (extensionStart > 0) {
                    val fileExtension = filename.substring(extensionStart)

                    // Check that the file has an extension we care about before trying to read out of it.
                    if (Game.extensions.contains(fileExtension.lowercase(Locale.getDefault()))) {
                        games.add(getGame(filename))
                    }
                }
            }
        }

        return games
    }

    private fun getGame(filePath: String): Game {
        var name = NativeLibrary.GetTitle(filePath)

        // If the game's title field is empty, use the filename.
        if (name.isEmpty()) {
            name = filePath.substring(filePath.lastIndexOf("/") + 1)
        }
        var gameId = NativeLibrary.GetGameId(filePath)

        // If the game's ID field is empty, use the filename without extension.
        if (gameId.isEmpty()) {
            gameId = filePath.substring(
                filePath.lastIndexOf("/") + 1,
                filePath.lastIndexOf(".")
            )
        }

        return Game(
            name,
            NativeLibrary.GetDescription(filePath).replace("\n", " "),
            NativeLibrary.GetRegions(filePath),
            filePath,
            gameId,
            NativeLibrary.GetCompany(filePath)
        )
    }
}
