package com.wegame.mmorpg.logic;

import com.wegame.framework.packet.IPacket;
import com.wegame.framework.packet.Packet;
import com.wegame.mmorpg.constants.RoleState;
import com.wegame.mmorpg.entity.PlayerEntity;
import com.wegame.mmorpg.model.AOIBlock;
import com.wegame.mmorpg.model.AOIPoint;
import com.wegame.mmorpg.model.Vector3;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class AOISystem {
    private final int AOISize = 48;
    private int blockSize = 0;
    private int mapSize; // 暂时支持正方形, 表示我们当前地图 mapSize * mapSize; --->
    private int AOIBlockNum = 0;
    private AOIBlock[][] AOIBlocksData = null;

    public void init(int mapSize, int blockSize) {
        this.blockSize = this.AOISize / 3; // 由视野大小，算出AOI每个小块大小;
        this.mapSize = mapSize * blockSize; // SGYD ---> 64 * 4 = 256;

        this.AOIBlockNum = this.mapSize / this.blockSize;
        this.AOIBlocksData = new AOIBlock[this.AOIBlockNum][this.AOIBlockNum];
        for (int z = 0; z < this.AOIBlockNum; z++) {
            for (int x = 0; x < this.AOIBlockNum; x++) {
                this.AOIBlocksData[z][x] = new AOIBlock();
            }
        }
    }

    /**
     * 把消息发送给entities的玩家，同时通过toPlayers获取区域内的玩家
     *
     * @param entities
     * @param msg
     * @param toPlayers
     * @param ignoreId
     */
    private void sendMsgInMap(List<PlayerEntity> entities, IPacket msg,
                              ArrayList<PlayerEntity> toPlayers, long ignoreId) {

        for (PlayerEntity entity : entities) {
            if (entity.getId() != ignoreId) {
                if (toPlayers != null) {
                    toPlayers.add(entity);
                }
                entity.sendMessage(msg);
            }
        }

    }

    /**
     * 把msg同步给以blockx,blocky为中心的9宫格区域的除ignoreId的玩家，同时把此区域的所有玩家实体加入到toPlayers中
     *
     * @param blockX
     * @param blockY
     * @param msg
     * @param toPlayers
     * @param ignoreId
     */
    public void sendMsgToAOIPlayer(int blockX, int blockY, IPacket msg,
                                   ArrayList<PlayerEntity> toPlayers, long ignoreId) {
        // 当前中心块
        this.sendMsgInMap(this.AOIBlocksData[blockX][blockY].getEntities(), msg, toPlayers,
                ignoreId);
        // end

        // 左
        if (blockX - 1 >= 0) {
            this.sendMsgInMap(this.AOIBlocksData[blockX - 1][blockY].getEntities(), msg, toPlayers,
                    ignoreId);
        }
        // end

        // 右
        if (blockX + 1 < this.AOIBlockNum) {
            this.sendMsgInMap(this.AOIBlocksData[blockX + 1][blockY].getEntities(), msg, toPlayers,
                    ignoreId);
        }
        // end

        // 上
        if (blockY + 1 < this.AOIBlockNum) {
            this.sendMsgInMap(this.AOIBlocksData[blockX][blockY + 1].getEntities(), msg, toPlayers,
                    ignoreId);
        }
        // end

        // 下
        if (blockY - 1 >= 0) {
            this.sendMsgInMap(this.AOIBlocksData[blockX][blockY - 1].getEntities(), msg, toPlayers,
                    ignoreId);
        }
        // end

        // 左上
        if (blockX - 1 >= 0 && blockY + 1 < this.AOIBlockNum) {
            this.sendMsgInMap(this.AOIBlocksData[blockX - 1][blockY + 1].getEntities(), msg,
                    toPlayers, ignoreId);
        }
        // end

        // 左下
        if (blockX - 1 >= 0 && blockY - 1 >= 0) {
            this.sendMsgInMap(this.AOIBlocksData[blockX - 1][blockY - 1].getEntities(), msg,
                    toPlayers, ignoreId);
        }
        // end

        // 右上
        if (blockX + 1 < this.AOIBlockNum && blockY + 1 < this.AOIBlockNum) {
            this.sendMsgInMap(this.AOIBlocksData[blockX + 1][blockY + 1].getEntities(), msg,
                    toPlayers, ignoreId);
        }
        // end

        // 右下
        if (blockX + 1 < this.AOIBlockNum && blockY - 1 >= 0) {
            this.sendMsgInMap(this.AOIBlocksData[blockX + 1][blockY - 1].getEntities(), msg,
                    toPlayers, ignoreId);
        }
        // end
    }

    /**
     * 向以坐标point为中心的9宫格区域的所有玩家发送信息，同时把区域的所有玩家加入toPlayers
     *
     * @param point
     * @param msg
     * @param toPlayers
     * @param ignoreId
     */
    public void sendMsgToAOIPlayer(Vector3 point, IPacket msg, ArrayList<PlayerEntity> toPlayers,
                                   long ignoreId) {
        int blockX = ((int) point.getX()) / this.blockSize;
        int blockY = ((int) point.getZ()) / this.blockSize;

        if (blockX < 0 || blockX >= this.AOIBlockNum ||
                blockY < 0 || blockY >= this.AOIBlockNum) {
            return;
        }

        this.sendMsgToAOIPlayer(blockX, blockY, msg, toPlayers, ignoreId);
    }

    /**
     * 把entity周围的实体信息同步给entity
     *
     * @param entities
     * @param entity
     */
    private void syncOtherRunOptToSelf(ArrayList<PlayerEntity> entities, PlayerEntity entity) {

        for (PlayerEntity playerEntity : entities) {
            if (playerEntity.getId() == entity.getId()) {
                continue;
            }
            if (playerEntity.getState() == RoleState.Run) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("transform", playerEntity.getTransformInfo());
                data.put("player", playerEntity.getId());
                if (playerEntity.getAStarComponent() != null) {
                    float dstX = playerEntity.getAStarComponent().getRoadPoints()[
                            playerEntity.getAStarComponent().getRoadPoints().length - 1].getX();
                    float dstZ = playerEntity.getAStarComponent().getRoadPoints()[
                            playerEntity.getAStarComponent().getRoadPoints().length - 1].getZ();
                    data.put("x", (int) (dstX * (1 << 16)));
                    data.put("y", (int) (dstZ * (1 << 16)));
                } else if (playerEntity.getJoystickComponent() != null) {
                    data.put("x", playerEntity.getJoystickComponent().getXPos());
                    data.put("y", playerEntity.getJoystickComponent().getYPos());
                }
            }

        }
    }

    private boolean checkBlock(PlayerEntity entity) {
        int blockX = ((int) entity.getTransformInfo().getPosition().getX()) / this.blockSize;
        int blockY = ((int) entity.getTransformInfo().getPosition().getZ()) / this.blockSize;

        if (blockX < 0 || blockX >= this.AOIBlockNum ||
                blockY < 0 || blockY >= this.AOIBlockNum) {
            return false;
        }
        entity.getAoiComponent().setBlockX(blockX);
        entity.getAoiComponent().setBlockY(blockY);
        return true;
    }

    /**
     * 首次进入场景，加入AoiMgr管理
     *
     * @param entity
     */
    public void AddToAOIMgr(PlayerEntity entity) {
        if (!checkBlock(entity)) {
            return;
        }
        // 通知AOI区域其他玩家，entity来了;
        HashMap<String, Object> data = new HashMap<>();
        ArrayList<PlayerEntity> ghostPlayers = new ArrayList<>();
        ghostPlayers.add(entity);
        data.put("ghostPlayers", ghostPlayers);
        // end
        // 把周围的玩家的信息发给entity
        this.syncOtherRunOptToSelf(ghostPlayers, entity);
        // end

        // 把自己记录到这里, 正式加入了
        this.AOIBlocksData[entity.getAoiComponent().getBlockX()][entity.getAoiComponent()
                .getBlockY()].getEntities().add(entity);
        // end
    }

    public void LeaveAOIMgr(PlayerEntity entity) {
        if (!checkBlock(entity)) {
            return;
        }
        this.AOIBlocksData[entity.getAoiComponent().getBlockX()][entity.getAoiComponent()
                .getBlockY()].getEntities().remove(entity);

        // 告诉周为所有的人，我已经离开了;
        List<Integer> leavePlayers = new ArrayList<>();
        leavePlayers.add(entity.getId());
        HashMap<String, Object> data = new HashMap<>();
        data.put("leavePlayers", leavePlayers);
        // end
    }

    /**
     * 判断value是否在list中
     *
     * @param list
     * @param value
     * @return
     */
    private boolean isInList(ArrayList<AOIPoint> list, AOIPoint value) {
        for (AOIPoint aoiPoint : list) {
            if (aoiPoint.getBlockX() == value.getBlockX() &&
                    aoiPoint.getBlockY() == value.getBlockY()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取blockx,blocky为中心的区域内的所有点
     *
     * @param blockX
     * @param blockY
     * @param points
     */
    private void getAOIPoints(int blockX, int blockY, ArrayList<AOIPoint> points) {
        points.add(new AOIPoint(blockX, blockY));
        // 左
        if (blockX - 1 >= 0) {
            points.add(new AOIPoint(blockX - 1, blockY));
        }
        // end

        // 右
        if (blockX + 1 < this.AOIBlockNum) {
            points.add(new AOIPoint(blockX + 1, blockY));
        }
        // end

        // 上
        if (blockY + 1 < this.AOIBlockNum) {
            points.add(new AOIPoint(blockX, blockY + 1));
        }
        // end

        // 下
        if (blockY - 1 >= 0) {
            points.add(new AOIPoint(blockX, blockY - 1));
        }
        // end

        // 左上
        if (blockX - 1 >= 0 && blockY + 1 < this.AOIBlockNum) {
            points.add(new AOIPoint(blockX - 1, blockY + 1));
        }
        // end

        // 左下
        if (blockX - 1 >= 0 && blockY - 1 >= 0) {
            points.add(new AOIPoint(blockX - 1, blockY - 1));
        }
        // end

        // 右上
        if (blockX + 1 < this.AOIBlockNum && blockY + 1 < this.AOIBlockNum) {
            points.add(new AOIPoint(blockX + 1, blockY + 1));
        }
        // end

        // 右下
        if (blockX + 1 < this.AOIBlockNum && blockY - 1 >= 0) {
            points.add(new AOIPoint(blockX + 1, blockY - 1));
        }
        // end
    }

    /**
     * 把packet的同步给entities
     *
     * @param entities
     * @param packet
     * @param ignoreId
     */
    private void sendMsgToEntityList(ArrayList<PlayerEntity> entities, IPacket packet,
                                     long ignoreId) {
        for (PlayerEntity entity : entities) {
            if (entity.getId() != ignoreId) {
                entity.sendMessage(packet);
            }

        }
    }

    /**
     * 如果entity正在行走，把状态同步给周围的其他实体
     *
     * @param entities
     * @param entity
     */
    private void syncSelfRunOptToOthers(ArrayList<PlayerEntity> entities, PlayerEntity entity) {
        // 如果我正在行走，给这些新玩家，发送一个导航事件;
        if (entity.getState() == RoleState.Run) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("transformInfo", entity.getTransformInfo());
            data.put("playerId", entity.getId());
            if (entity.getAStarComponent() != null) {
                float dstX = entity.getAStarComponent().getRoadPoints()[
                        entity.getAStarComponent().getRoadPoints().length - 1].getX();
                float dstZ = entity.getAStarComponent().getRoadPoints()[
                        entity.getAStarComponent().getRoadPoints().length - 1].getZ();
                data.put("x", dstX * (1 << 16));
                data.put("y", dstZ * (1 << 16));
            } else if (entity.getJoystickComponent() != null) {
                data.put("x", entity.getJoystickComponent().getXPos());
                data.put("y", entity.getJoystickComponent().getYPos());
            }
        }
        // end
    }

    public void update(PlayerEntity entity) {
        int blockX = ((int) entity.getTransformInfo().getPosition().getX()) / this.blockSize;
        int blockY = ((int) entity.getTransformInfo().getPosition().getZ()) / this.blockSize;

        if (blockX < 0 || blockX >= this.AOIBlockNum ||
                blockY < 0 || blockY >= this.AOIBlockNum) {
            return;
        }
        // entity 的AOI不会有任何变化
        if (entity.getAoiComponent().getBlockX() == blockX &&
                entity.getAoiComponent().getBlockY() == blockY) {
            return;
        }
        // end

        // 变化前AOI区域
        ArrayList<AOIPoint> oldAOIBlocks = new ArrayList<>();
        this.getAOIPoints(entity.getAoiComponent().getBlockX(),
                entity.getAoiComponent().getBlockY(), oldAOIBlocks);
        // 变化后AOI区域
        ArrayList<AOIPoint> newAOIBlocks = new ArrayList<>();
        this.getAOIPoints(blockX, blockY, newAOIBlocks);
        // end

        // 告诉在oldList,但不在newList的玩家，我离开了
        ArrayList<Integer> leavePlayers = new ArrayList<>();
        leavePlayers.add(entity.getId());
        HashMap<String, Object> data = new HashMap<>();
        data.put("leavePlayers", leavePlayers);
        ArrayList<PlayerEntity> toPlayers = new ArrayList<>();
        for (AOIPoint value : oldAOIBlocks) {
            if (!this.isInList(newAOIBlocks, value)) {
            }
        }
        // end

        // 告诉entity, oldList玩家不在了
        if (toPlayers.size() > 0) {
            leavePlayers = new ArrayList<>();
            for (PlayerEntity playerEntity : toPlayers) {
                leavePlayers.add(playerEntity.getId());
            }
            data = new HashMap();
            data.put("leavePlayers", leavePlayers);
            // end
        }

        // 告诉在newList, 但不在oldList的玩家，entity来了
        List<PlayerEntity> ghostPlayers = new ArrayList<>();
        ghostPlayers.add(entity);
        data = new HashMap();
        data.put("ghostPlayers", ghostPlayers);
        toPlayers = new ArrayList<>();
        for (AOIPoint value : newAOIBlocks) {
            if (!this.isInList(oldAOIBlocks, value)) {
            }
            // end
        }
        // 发给entity，周围新进来的角色
        if (!toPlayers.isEmpty()) {
            HashMap<String, Object> pack = new HashMap<>();
            pack.put("ghostPlayers", toPlayers);
            IPacket packet = new Packet((short) 0, (short) 100);
            entity.sendMessage(packet);
            // 如果entity正在行走，给这些新玩家，发送一个导航事件;
            this.syncSelfRunOptToOthers(toPlayers, entity);
            // end

            // 其他玩家正在跑的，也要发给entity;
            this.syncOtherRunOptToSelf(toPlayers, entity);
            // end
        }
        // end

        // 更新entity所在的块的位置
        this.AOIBlocksData[entity.getAoiComponent().getBlockX()][entity.getAoiComponent()
                .getBlockY()].getEntities().remove(entity);
        this.AOIBlocksData[blockX][blockY].getEntities().add(entity);
        entity.getAoiComponent().setBlockX(blockX);
        entity.getAoiComponent().setBlockY(blockY);
        // end
    }
}
