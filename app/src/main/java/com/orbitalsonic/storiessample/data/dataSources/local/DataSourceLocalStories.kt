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
                headerUrl = "https://images.unsplash.com/photo-1542816417-0983c9c9ad53?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80",
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
                headerUrl = "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80",
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
                headerUrl = "https://images.unsplash.com/photo-1539650116574-75c0c6d73c6e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80",
                storyList = listOf(
                    MyStory(url = "https://www.shutterstock.com/image-photo/masjid-aqsa-moonlight-setting-ethereal-600w-2572272915.jpg"),
                    MyStory(url = "https://img.freepik.com/premium-photo/picture-building-with-moon-sky-masjid-alaqsa-palestine_979520-47408.jpg"),
                    MyStory(url = "https://www.shutterstock.com/image-illustration/palestine-background-masjid-al-aqsa-600nw-2549758797.jpg"),
                    MyStory(url = "https://legacy.institute/wp-content/uploads/2022/10/History-of-Masjid-Al-Aqsa.jpg")
                )
            ),
            ItemStory(
                id = 3,
                headerText = "Istanbul",
                subHeaderText = "City of Two Continents",
                headerUrl = "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80",
                storyList = listOf(
                    MyStory(url = "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://i.pinimg.com/736x/2d/95/e5/2d95e5886fc4c65a6778b5fee94a7d59.jpg"),
                    MyStory(url = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80")
                )
            ),
            ItemStory(
                id = 4,
                headerText = "Dubai",
                subHeaderText = "City of Gold",
                headerUrl = "https://cdn.pixabay.com/photo/2016/11/21/06/53/beautiful-natural-image-1844362_640.jpg",
                storyList = listOf(
                    MyStory(url = "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1586444248902-2f64eddc13df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1559827260-dc66d52bef19?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1571115764595-644a1f56a55c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80")
                )
            ),
            ItemStory(
                id = 5,
                headerText = "Cairo",
                subHeaderText = "City of a Thousand Minarets",
                headerUrl = "https://images.unsplash.com/photo-1586444248902-2f64eddc13df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80",
                storyList = listOf(
                    MyStory(url = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1501594907352-04cda38ebc29?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1501594907352-04cda38ebc29?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80")
                )
            ),
            ItemStory(
                id = 6,
                headerText = "Marrakech",
                subHeaderText = "Red City of Morocco",
                headerUrl = "https://images.unsplash.com/photo-1559827260-dc66d52bef19?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80",
                storyList = listOf(
                    MyStory(url = "https://images.unsplash.com/photo-1539650116574-75c0c6d73c6e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80")
                )
            ),
            ItemStory(
                id = 7,
                headerText = "Kuala Lumpur",
                subHeaderText = "Garden City of Lights",
                headerUrl = "https://images.unsplash.com/photo-1571115764595-644a1f56a55c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80",
                storyList = listOf(
                    MyStory(url = "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1586444248902-2f64eddc13df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1559827260-dc66d52bef19?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1571115764595-644a1f56a55c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80")
                )
            ),
            ItemStory(
                id = 8,
                headerText = "Lahore",
                subHeaderText = "Heart of Pakistan",
                headerUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80",
                storyList = listOf(
                    MyStory(url = "https://images.unsplash.com/photo-1539650116574-75c0c6d73c6e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"),
                    MyStory(url = "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80")
                )
            )
        )
    }
}
