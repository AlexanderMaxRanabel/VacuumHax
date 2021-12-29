package mathax.legacy.client.systems.modules.chat;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.game.ReceiveMessageEvent;
import mathax.legacy.client.events.game.SendMessageEvent;
import mathax.legacy.client.mixin.ChatHudAccessor;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.settings.StringSetting;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/*/-----------------/*/
/*/ Made by NobreHD /*/
/*/-----------------/*/

public class ChatEncryption extends Module {
    private static final String password = "MatHaxEncryption";

    private static SecretKeySpec secretKey;

    private static byte[] key;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> encryptAll = sgGeneral.add(new BoolSetting.Builder()
        .name("encrypt-all")
        .description("Encrypts all sent messages.")
        .defaultValue(false)
        .build()
    );

    private final Setting<String> prefix = sgGeneral.add(new StringSetting.Builder()
        .name("prefix")
        .description("The prefix determining which messages will get encrypted.")
        .defaultValue(";")
        .visible(() -> !encryptAll.get())
        .build()
    );

    private final Setting<String> suffix = sgGeneral.add(new StringSetting.Builder()
        .name("suffix")
        .description("The prefix determining how will encrypted messages end.")
        .defaultValue("<3")
        .build()
    );

    private final Setting<Boolean> customKey = sgGeneral.add(new BoolSetting.Builder()
        .name("custom-key")
        .description("Allow you to use a custom key.")
        .defaultValue(true)
        .build()
    );

    private final Setting<String> groupKey = sgGeneral.add(new StringSetting.Builder()
        .name("key")
        .description("The key to use.")
        .defaultValue("MatHaxEncryption")
        .visible(customKey::get)
        .build()
    );

    public ChatEncryption(){
        super(Categories.Client, Items.BARRIER, "chat-encryption", "Encrypts your chat messages.");
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getVisibleMessages().removeIf((message) -> message.getId() == event.id && event.id != 0);
        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getMessages().removeIf((message) -> message.getId() == event.id && event.id != 0);

        Text message = event.getMessage();
        if (message.getString().endsWith(suffix.get())){
            String[] msg = message.getString().split(" ",2);
            msg[1] = decrypt(msg[1], customKey.get() ? groupKey.get() : password);
            message = new LiteralText(msg[0] + " " + msg[1]);
        }

        event.setMessage(message);
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) {
        String message = event.message;

        if (encryptAll.get() || message.startsWith(prefix.get())) message = messageSendUtil(message);

        event.message = message;
    }

    private String messageSendUtil(String message) {
        message = message.substring(prefix.get().length());
        return encrypt(message, (customKey.get() ? groupKey.get() : password));
    }

    public static void setKey(String myKey) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = Arrays.copyOf(sha.digest(myKey.getBytes(StandardCharsets.UTF_8)), 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }
    }

    public String encrypt(String strToEncrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8))).replace("==", suffix.get());
        } catch (Exception exception) {
            error("Error while encrypting: " + exception);
        }

        return null;
    }

    public String decrypt(String strToDecrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt.replace(suffix.get(), "=="))), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            error("Error while decrypting: " + exception);
        }

        return strToDecrypt;
    }
}