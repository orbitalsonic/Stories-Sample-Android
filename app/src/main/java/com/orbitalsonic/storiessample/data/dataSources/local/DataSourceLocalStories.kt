package com.orbitalsonic.storiessample.data.dataSources.local

import com.orbitalsonic.storiessample.data.entities.ItemStory
import dev.epegasus.storyview.dataClasses.MyStory

class DataSourceLocalStories {

    fun getStories(): List<ItemStory> {
        return listOf(
            ItemStory(
                id = 0,
                headerText = "Makkah",
                subHeaderText = "Home of Allah",
                headerUrl = "https://picsum.photos/id/1/300/200",
                storyList = listOf(
                    MyStory(url = "https://picsum.photos/id/1/300/200"),
                    MyStory(url = "https://picsum.photos/id/2/300/200"),
                    MyStory(url = "https://picsum.photos/id/3/300/200"),
                    MyStory(url = "https://picsum.photos/id/4/300/200")
                )
            ),
            ItemStory(
                id = 1,
                headerText = "Madina",
                subHeaderText = "Home of our beloved Prophet (P.B.U.H)",
                headerUrl = "https://picsum.photos/id/5/300/200",
                storyList = listOf(
                    MyStory(url = "https://picsum.photos/id/5/300/200"),
                    MyStory(url = "https://picsum.photos/id/6/300/200"),
                    MyStory(url = "https://picsum.photos/id/7/300/200"),
                    MyStory(url = "https://picsum.photos/id/8/300/200")
                )
            ),
            ItemStory(
                id = 2,
                headerText = "Masjid Al Aqsa",
                subHeaderText = "Temporary Qibla",
                headerUrl = "https://picsum.photos/id/9/300/200",
                storyList = listOf(
                    MyStory(url = "https://picsum.photos/id/9/300/200"),
                    MyStory(url = "https://picsum.photos/id/10/300/200"),
                    MyStory(url = "https://picsum.photos/id/11/300/200"),
                    MyStory(url = "https://picsum.photos/id/12/300/200")
                )
            ),
            ItemStory(
                id = 3,
                headerText = "Istanbul",
                subHeaderText = "City of Two Continents",
                headerUrl = "https://picsum.photos/id/13/300/200",
                storyList = listOf(
                    MyStory(url = "https://picsum.photos/id/13/300/200"),
                    MyStory(url = "https://picsum.photos/id/14/300/200"),
                    MyStory(url = "https://picsum.photos/id/15/300/200"),
                    MyStory(url = "https://picsum.photos/id/16/300/200")
                )
            ),
            ItemStory(
                id = 4,
                headerText = "Dubai",
                subHeaderText = "City of Gold",
                headerUrl = "https://picsum.photos/id/17/300/200",
                storyList = listOf(
                    MyStory(url = "https://picsum.photos/id/17/300/200"),
                    MyStory(url = "https://picsum.photos/id/18/300/200"),
                    MyStory(url = "https://picsum.photos/id/19/300/200"),
                    MyStory(url = "https://picsum.photos/id/20/300/200")
                )
            ),
            ItemStory(
                id = 5,
                headerText = "Cairo",
                subHeaderText = "City of a Thousand Minarets",
                headerUrl = "https://picsum.photos/id/21/300/200",
                storyList = listOf(
                    MyStory(url = "https://picsum.photos/id/21/300/200"),
                    MyStory(url = "https://picsum.photos/id/22/300/200"),
                    MyStory(url = "https://picsum.photos/id/23/300/200"),
                    MyStory(url = "https://picsum.photos/id/24/300/200")
                )
            ),
            ItemStory(
                id = 6,
                headerText = "Marrakech",
                subHeaderText = "Red City of Morocco",
                headerUrl = "https://picsum.photos/id/25/300/200",
                storyList = listOf(
                    MyStory(url = "https://picsum.photos/id/25/300/200"),
                    MyStory(url = "https://picsum.photos/id/26/300/200"),
                    MyStory(url = "https://picsum.photos/id/27/300/200"),
                    MyStory(url = "https://picsum.photos/id/28/300/200")
                )
            ),
            ItemStory(
                id = 7,
                headerText = "Kuala Lumpur",
                subHeaderText = "Garden City of Lights",
                headerUrl = "https://picsum.photos/id/29/300/200",
                storyList = listOf(
                    MyStory(url = "https://picsum.photos/id/29/300/200"),
                    MyStory(url = "https://picsum.photos/id/30/300/200"),
                    MyStory(url = "https://picsum.photos/id/31/300/200"),
                    MyStory(url = "https://picsum.photos/id/32/300/200")
                )
            ),
            ItemStory(
                id = 8,
                headerText = "Lahore",
                subHeaderText = "Heart of Pakistan",
                headerUrl = "https://picsum.photos/id/33/300/200",
                storyList = listOf(
                    MyStory(url = "https://picsum.photos/id/33/300/200"),
                    MyStory(url = "https://picsum.photos/id/34/300/200"),
                    MyStory(url = "https://picsum.photos/id/35/300/200"),
                    MyStory(url = "https://picsum.photos/id/36/300/200")
                )
            )
        )
    }
}
