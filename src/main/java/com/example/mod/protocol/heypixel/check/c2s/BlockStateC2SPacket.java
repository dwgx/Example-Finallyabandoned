package com.example.mod.protocol.heypixel.check.c2s;

import com.example.mod.protocol.heypixel.HeypixelHandler;
import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.utils.BufferHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallPlayerSkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.value.Variable;

import java.io.IOException;

import static com.example.mod.client.GameAccessor.mc;


//@StringEncryption
//@ControlFlowObfuscation
public class BlockStateC2SPacket extends HeypixelPacket {


    public BlockState state;
    public String str;


    public BlockStateC2SPacket(BlockPos blockPos) {
        this.state = MinecraftClient.getInstance().world.getBlockState(blockPos);
//        if(state.getBlock() == Blocks.PLAYER_HEAD || state.getBlock() == Blocks.PLAYER_HEAD) {
//            this.str = ((SkullBlock)state.getBlock()).
//        }

        BlockEntity entity = mc.world.getBlockEntity(blockPos);
        if (entity instanceof SkullBlockEntity sb) {
            int rot = sb.getCachedState().get(SkullBlock.ROTATION);
            if (rot == 15) {
                rot = 0;
            } else {
                rot++;
            }
            rot = (rot / 4 + 2) % 4;
            Direction fac = switch (rot) {
                case 0 -> Direction.SOUTH;
                case 1 -> Direction.NORTH;
                case 2 -> Direction.WEST;
                case 3 -> Direction.EAST;
                default -> null;
            };

            str = "Block{" + sb.getOwner().name().get() + "}[facing=" +
                (
                    state.getBlock() instanceof WallPlayerSkullBlock
                        ? state.get(WallPlayerSkullBlock.FACING).getDirection().toString()
                        : fac.toString()
                )
                + ",type=single,waterlogged=false]";
        } else {
            str = state.toString();
        }
    }

    public static void send(HeypixelHandler manager, BlockPos blockPos) {
        new BlockStateC2SPacket(blockPos).set(manager).sendMsgpack();
    }

    @Override
    public void encode(PacketByteBuf friendlyByteBuf, BufferHelper bufferHelper) {
        bufferHelper.writeString(friendlyByteBuf, str);
//        System.out.println("state: " + str);
    }

    @Override
    public void encode(MessageBufferPacker packer) throws IOException {
        packer.packValue(new Variable().setStringValue(str));
//        System.out.println("state: " + str);
    }
}
