package tomikaa.greeremote.Gree.Deserializers;

import tomikaa.greeremote.Gree.Packs.BindOkPack;
import tomikaa.greeremote.Gree.Packs.BindPack;
import tomikaa.greeremote.Gree.Packs.DatPack;
import tomikaa.greeremote.Gree.Packs.DevicePack;
import tomikaa.greeremote.Gree.Packs.Pack;
import tomikaa.greeremote.Gree.Packs.ResultPack;
import tomikaa.greeremote.Gree.Packs.StatusPack;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/*
 * This file is part of GreeRemoteAndroid.
 *
 * GreeRemoteAndroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GreeRemoteAndroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GreeRemoteAndroid. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Created by tomikaa87 <https://github.com/tomikaa87> on 2017-11-26.
 */

public class PackDeserializer implements JsonDeserializer<Pack> {

    @Override
    public Pack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String packType = jsonObject.get("t").getAsString();

        if (packType.equalsIgnoreCase(BindOkPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, BindOkPack.class);
        } else if (packType.equalsIgnoreCase(BindPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, BindPack.class);
        } else if (packType.equalsIgnoreCase(DatPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, DatPack.class);
        } else if (packType.equalsIgnoreCase(ResultPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, ResultPack.class);
        } else if (packType.equalsIgnoreCase(StatusPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, StatusPack.class);
        } else if (packType.equalsIgnoreCase(DevicePack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, DevicePack.class);
        }

        return null;
    }
}
