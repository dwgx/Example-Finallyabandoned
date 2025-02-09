package com.example.mod.protocol.heypixel.check.c2s;


import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.utils.BufferHelper;
import com.example.mod.protocol.heypixel.utils.HeypixelVarUtils;
import net.minecraft.network.PacketByteBuf;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;
import org.msgpack.value.impl.ImmutableLongValueImpl;

import java.util.*;


//@StringEncryption
//@ControlFlowObfuscation
public class ClassesC2SPacket extends HeypixelPacket {
    public int size3;
    public int size2;
    public int size1;
    public int size;
    public int id;
    public Object obj;

    public ClassesC2SPacket(PacketByteBuf buf) {
        this.id = -2;
        this.size = -1;
        this.size1 = -1;
        this.size2 = -1;
        this.size3 = -1;
        this.obj = new HashSet();
    }

    public ClassesC2SPacket(int id, int size1, Object data, int size2, int size3, int size4) {
        this.id = id;
        this.size = size1;
        this.obj = data;
        this.size1 = size2;
        this.size2 = size3;
        this.size3 = size4;
    }

    @Override
    public void encode(PacketByteBuf buf, BufferHelper bufferHelper) {
        HeypixelVarUtils.writeVarInt(buf, this.id);
        HeypixelVarUtils.writeUnsignedInt(buf, this.size);
        HeypixelVarUtils.writeUnsignedInt(buf, this.size1);
        HeypixelVarUtils.writeUnsignedInt(buf, this.size2);
        HeypixelVarUtils.writeUnsignedInt(buf, this.size3);
        if (this.obj instanceof Set) {
            ArrayList<String> arrayList = new ArrayList<>((Set<String>) this.obj);
            Collections.shuffle(arrayList);
            bufferHelper.writeStringCollection(buf, arrayList.subList(0, Math.min(arrayList.size(), 30)));
        }
        Object obj = this.obj;
        if (obj instanceof HashMap hashMap) {
            bufferHelper.writeStringCollection(buf, new ArrayList<>(((HashMap<String, String>) hashMap).keySet()));
            bufferHelper.writeStringCollection(buf, new ArrayList<>(((HashMap<String, String>) hashMap).values()));
        }
    }

    @Override
    public void encode(MessageBufferPacker packer) {
        try {
            packer.packValue(new ImmutableLongValueImpl(this.id));
            packer.packValue(new ImmutableLongValueImpl(this.size));
            packer.packValue(new ImmutableLongValueImpl(this.size1));
            packer.packValue(new ImmutableLongValueImpl(this.size2));
            packer.packValue(new ImmutableLongValueImpl(this.size3));
            packer.packValue(new ImmutableLongValueImpl(System.currentTimeMillis()));

            if (this.obj instanceof Set) {
                ArrayList<String> classes = new ArrayList<>((Set<String>) this.obj);
                Collections.shuffle(classes);
                List<String> subbedClasses = classes.subList(0, Math.min(classes.size(), 20));
                ArrayList<Value> classesList = new ArrayList<>();
                for (var o : subbedClasses) {
                    classesList.add(ValueFactory.newString(o));
                }

                packer.packValue(ValueFactory.newArray(classesList));
            } else {
                Object obj = this.obj;
                if (obj instanceof HashMap map) {
                    List<Map.Entry<String, String>> classes = new ArrayList<>(map.entrySet());
                    Collections.shuffle(classes);
                    List<Map.Entry<String, String>> subbedClasses = classes.subList(0, Math.min(20, classes.size()));
                    HashMap<Value, Value> classesMap = new HashMap<>();
                    for (Map.Entry<String, String> entry : subbedClasses) {
                        classesMap.put(
                            ValueFactory.newString(entry.getKey()),
                            ValueFactory.newString(
                                entry.getValue() == null ? "null" : entry.getValue()
                            ));
                    }
                    packer.packValue(ValueFactory.newMap(classesMap));
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
