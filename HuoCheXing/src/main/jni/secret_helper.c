#include <string.h>
#include <jni.h>
jstring
Java_com_sshine_huochexing_utils_AESCrypt_getSeedChar( JNIEnv* env,
        jobject thiz )
{

    return (*env)->NewStringUTF(env, "De3GH3y9;x65");
}
