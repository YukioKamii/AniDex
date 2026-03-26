package com.example.anidex

import com.apollographql.apollo.ApolloClient

object ApolloClientProvider {
    val client: ApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl("https://graphql.anilist.co")
            .build()
    }
}