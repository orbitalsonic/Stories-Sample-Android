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
                headerUrl = "https://images.pexels.com/photos/36029/aroni-arsa-children-little.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                storyList = listOf(
                    MyStory(url = "https://m.ahstatic.com/is/image/accorhotels/aja_p_5829-05:8by10?fmt=jpg&op_usm=1.75,0.3,2,0&resMode=sharp2&iccEmbed=true&icc=sRGB&dpr=on,1.5&wid=335&hei=418&qlt=80"),
                    MyStory(url = "https://hotelsplatform.com/uploads/0000/52/2022/02/21/kaaba.jpg"),
                    MyStory(url = "https://thumbs.dreamstime.com/b/beautiful-view-kaaba-makkah-saudia-arab-181925338.jpg"),
                    MyStory(url = "https://upload.wikimedia.org/wikipedia/en/thumb/3/38/Makkah_Clock_Tower.jpg/960px-Makkah_Clock_Tower.jpg")
                )
            ),
            ItemStory(
                id = 1,
                headerText = "Madina",
                subHeaderText = "Home of our beloved Prophet (P.B.U.H)",
                headerUrl = "https://images.pexels.com/photos/36029/aroni-arsa-children-little.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                storyList = listOf(
                    MyStory(url = "https://w0.peakpx.com/wallpaper/404/60/HD-wallpaper-madina-islam-madina.jpg"),
                    MyStory(url = "https://img.freepik.com/free-photo/mosque-building-architecture-with-eery-weather_23-2150999879.jpg?semt=ais_incoming&w=740&q=80"),
                    MyStory(url = "https://hotelsplatform.com/uploads/0000/6/2021/12/20/madina-el-monawara.jpg"),
                    MyStory(url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTV6ff6EoPudDOJzpwd23U_Mgto63bdHBXB0A&s")
                )
            ),
            ItemStory(
                id = 2,
                headerText = "Masjid Al Aqsa",
                subHeaderText = "Temporary Qibla",
                headerUrl = "https://images.pexels.com/photos/36029/aroni-arsa-children-little.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                storyList = listOf(
                    MyStory(url = "https://www.shutterstock.com/image-photo/masjid-aqsa-moonlight-setting-ethereal-600w-2572272915.jpg"),
                    MyStory(url = "https://img.freepik.com/premium-photo/picture-building-with-moon-sky-masjid-alaqsa-palestine_979520-47408.jpg"),
                    MyStory(url = "https://www.shutterstock.com/image-illustration/palestine-background-masjid-al-aqsa-600nw-2549758797.jpg"),
                    MyStory(url = "https://legacy.institute/wp-content/uploads/2022/10/History-of-Masjid-Al-Aqsa.jpg")
                )
            )
        )
    }
}
