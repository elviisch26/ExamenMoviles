/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.playlist

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.playlist.data.PlaylistDatabase
import com.example.playlist.data.Playlist
import com.example.playlist.data.PlaylistDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PlaylistDaoTest {

    private lateinit var playlistDao: PlaylistDao
    private lateinit var playlistDatabase: PlaylistDatabase
    private val playlist1 = Playlist(1, "Game", "uwu", "xd", 20 )
    private val playlist2 = Playlist(2, "Game2", "uwu2", "xd2", 201 )

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        playlistDatabase = Room.inMemoryDatabaseBuilder(context, PlaylistDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        playlistDao = playlistDatabase.itemDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        playlistDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        addOneItemToDb()
        val allItems = playlistDao.getAllItems().first()
        assertEquals(allItems[0], playlist1)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        val allItems = playlistDao.getAllItems().first()
        assertEquals(allItems[0], playlist1)
        assertEquals(allItems[1], playlist2)
    }


    @Test
    @Throws(Exception::class)
    fun daoGetItem_returnsItemFromDB() = runBlocking {
        addOneItemToDb()
        val item = playlistDao.getItem(1)
        assertEquals(item.first(), playlist1)
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteItems_deletesAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        playlistDao.delete(playlist1)
        playlistDao.delete(playlist2)
        val allItems = playlistDao.getAllItems().first()
        assertTrue(allItems.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdateItems_updatesItemsInDB() = runBlocking {
        addTwoItemsToDb()
        playlistDao.update(Playlist(1, "Game", "uwu", "xd", 20 ),)
        playlistDao.update(Playlist(2, "Game2", "uwu2", "xd2", 201 ),)

        val allItems = playlistDao.getAllItems().first()
        assertEquals(allItems[0], Playlist(1, "Game", "uwu", "xd", 20 ))
        assertEquals(allItems[1], Playlist(2, "Game2", "uwu2", "xd2", 201 ))
    }

    private suspend fun addOneItemToDb() {
        playlistDao.insert(playlist1)
    }

    private suspend fun addTwoItemsToDb() {
        playlistDao.insert(playlist1)
        playlistDao.insert(playlist2)
    }
}
