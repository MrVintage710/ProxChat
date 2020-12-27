package me.mrvintage.prox.bot;

import com.google.common.io.ByteSource;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.org.apache.bcel.internal.generic.LAND;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.commons.compress.utils.Charsets;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BotCommands extends ListenerAdapter {

    private static final HashMap<UUID, Long> players = new HashMap<>();

    private static final String GROUP_NAME = "Minecraft";
    private static final String LANDING_CHANNEL_NAME = "Prox Chat";
    private static final String COMMAND_CHANNEL_NAME = "Commands";

    private static long catagory_id = 0L;
    private static long landing_channel_id = 0L;
    private static long command_channel_id = 0L;

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        System.out.println(event.getJDA().getSelfUser() + " is Ready.");

        List<Category> categories = event.getJDA().getCategoriesByName(GROUP_NAME, true);
        if(categories.isEmpty()) {
            throw new IllegalStateException("Can not find the Catagory '" + GROUP_NAME + "' in server.");
        } else {
            catagory_id = categories.get(0).getIdLong();
        }

        Category category = getGroup(event.getJDA());

        landing_channel_id = createVoiceChannel(category, LANDING_CHANNEL_NAME);
        command_channel_id = createTextChannel(category, COMMAND_CHANNEL_NAME);
        System.out.println(landing_channel_id + " | " + command_channel_id);
        deleteOther(category, landing_channel_id, command_channel_id);

        try {
            fetchUUID("MrVintage710");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShutdown(@Nonnull ShutdownEvent event) {
        System.out.println("Ending");
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() == command_channel_id) System.out.println(event.getAuthor().getName() + " said " + event.getMessage().getContentRaw());

    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {

    }

    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        Member joiner = event.getMember();

        if(event.getChannelJoined().getIdLong() == landing_channel_id) {
            if(isLinked(joiner.getIdLong())) {

            } else {
                joiner.getGuild().kickVoiceMember(joiner).complete();
                joiner.getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("You must link your minecraft account to the bot to be in prox chat. Respond with your minecraft user name to link this account with your minecraft account.").queue();
                });
            }
        }

        System.out.println(event.getMember());
        //if(isLinked(event))
    }

    private Category getGroup(JDA jda) {
        if(catagory_id != 0) {
            return jda.getCategoryById(catagory_id);
        }

        return null;
    }

    private long createTextChannel(Category category, String name) {
        long id = 0L;
        for(TextChannel channel : category.getTextChannels()) {
            if(channel.getName().equals(name)) {
                id = channel.getIdLong();
                break;
            }
        }

        if(id == 0L) id = category.createTextChannel(name).complete().getIdLong();

        return id;
    }

    private long createVoiceChannel(Category category, String name) {
        long id = 0L;
        for(VoiceChannel channel : category.getVoiceChannels()) {
            if(channel.getName().equals(name)) {
                id = channel.getIdLong();
                break;
            }
        }

        if(id == 0L) id = category.createVoiceChannel(name).complete().getIdLong();

        return id;
    }

    private void deleteOther(Category category, long... ids) {
        for(GuildChannel channel : category.getChannels()) {
            if(Arrays.stream(ids).anyMatch(value -> value == channel.getIdLong())) continue;
            System.out.println("Deleting " + channel.getName());
            channel.delete().queue();
        }
    }

    private boolean isLinked(long playerID) {
        return players.values().stream().anyMatch(value -> value == playerID);
    }

    private String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    private String fetchUUID(String username) throws IOException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if(connection.getResponseCode() == 200) {
            InputStream stream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            return json.get("id").getAsString();
        } else if(connection.getResponseCode() == 204) {

        }

        connection.disconnect();
        return null;
    }
}
