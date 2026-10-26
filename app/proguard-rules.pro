# Regras do R8 para o build de release.

# Classes lidas pelo Gson: os nomes dos campos precisam continuar iguais
# aos do JSON que o servidor envia.
-keep class com.example.protege_idoso.IARiskAnalyzer$RespostaRegistro { *; }
-keep class com.example.protege_idoso.IARiskAnalyzer$RespostaServidor { *; }

# Remove os logs de depuração do APK final (os de aviso e erro continuam).
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
}

# Anotações usadas só durante a compilação pela biblioteca de criptografia
# do token (Tink). Sem isso o R8 acusa classes ausentes.
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
