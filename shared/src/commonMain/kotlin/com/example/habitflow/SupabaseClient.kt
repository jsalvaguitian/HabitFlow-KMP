package com.example.habitflow

import com.example.habitflow.config.BuildKonfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = BuildKonfig.SUPABASE_URL,
    supabaseKey = BuildKonfig.SUPABASE_PUBLISHABLE_KEY,
) {
    install(Postgrest)
}
