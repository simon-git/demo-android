/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Albert Moky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ==============================================================================
 */
package chat.dim.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.dim.client.Facebook;
import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.impl.PrivateKeyImpl;
import chat.dim.mkm.Address;
import chat.dim.mkm.ID;

public class UserTable extends ExternalStorage {

    private static List<ID> userList = new ArrayList<>();

    // "/sdcard/chat.dim.sechat/dim/users.js"

    private static String getUsersFilePath() {
        return root + "/dim/users.js";
    }

    private static boolean saveUsers() {
        String path = getUsersFilePath();
        try {
            return writeJSON(userList, path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void loadUsers() {
        String path = getUsersFilePath();
        List list = null;
        try {
            list = (List) readJSON(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list == null) {
            return;
        }
        Facebook facebook = Facebook.getInstance();
        ID user;
        for (Object item : list) {
            user = facebook.getID(item);
            if (userList.contains(user)) {
                continue;
            }
            userList.add(user);
        }
    }

    public static List<ID> allUsers() {
        return userList;
    }

    public static boolean addUser(ID user) {
        if (userList.contains(user)) {
            return false;
        }
        boolean added = userList.add(user);
        return added && saveUsers();
    }

    public static boolean removeUser(ID user) {
        if (!userList.contains(user)) {
            return false;
        }
        boolean removed = userList.remove(user);
        return removed && saveUsers();
    }

    public static void setCurrentUser(ID user) {
        int index = userList.indexOf(user);
        if (index == 0) {
            // already the first user
            return;
        }
        if (index > 0) {
            // already exists, but not the first user
            userList.remove(user);
        }
        userList.add(0, user);
        saveUsers();
    }

    public static ID getCurrentUser() {
        if (userList.size() > 0) {
            return userList.get(0);
        }
        return null;
    }

    //-------- Private Key

    private static Map<Address, PrivateKey> keys = new HashMap<>();

    // "/sdcard/chat.dim.sechat/.private/{address}/secret.js"

    private static String getKeyFilePath(Address address) {
        return root + "/.private/" + address + "/secret.js";
    }

    public static boolean savePrivateKey(PrivateKey key, ID user) {
        keys.put(user.address, key);
        String path = getKeyFilePath(user.address);
        try {
            return writeJSON(key, path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static PrivateKey loadKey(Address address) {
        // load from JsON file
        String path = getKeyFilePath(address);
        try {
            Object dict = readJSON(path);
            return PrivateKeyImpl.getInstance(dict);
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static PrivateKey getPrivateKeyForSignature(ID user) {
        PrivateKey key = keys.get(user.address);
        if (key == null) {
            key = loadKey(user.address);
            if (key != null) {
                keys.put(user.address, key);
            }
        }
        return key;
    }

    public static List<PrivateKey> getPrivateKeysForDecryption(ID user) {
        // FIXME: get private key matches profile key
        PrivateKey key = getPrivateKeyForSignature(user);
        if (key == null) {
            return null;
        }
        List<PrivateKey> keys = new ArrayList<>();
        keys.add(key);
        return keys;
    }

    static {
        // FIXME: test
        Facebook facebook = Facebook.getInstance();
        userList.add(facebook.getID("moki@4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk"));
    }
}