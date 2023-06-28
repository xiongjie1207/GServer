package com.wegame.mmorpg.logic;

import com.wegame.mmorpg.model.MapPoint;
import com.wegame.mmorpg.model.PathNode;
import com.wegame.mmorpg.model.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AStar {
    // 斜角移动花费
    public float ddCost = 14.14f;
    //直线移动花费
    public float dsCost = 10;
    /**
     * tile尺寸
     */
    private float size;
    /**
     * 地图宽
     */
    private int width;
    /**
     * 地图高
     */
    private int height;
    /**
     * 地图元数据
     */
    private boolean[][] goMap = null;
    /**
     * 地图路径数据
     */
    private PathNode[][] pathMap = null;
    private ArrayList<PathNode> openList;
    private ArrayList<PathNode> closeList;
    private ArrayList<PathNode> foundPath = null;
    private ArrayList<PathNode> afterFilteFoundPath = null;
    private PathNode startGridPointer;
    private PathNode targetGridPointer;
    private float ypos; // 返回给用户的y的坐标;

    public List<PathNode> getFoundPath() {
        return this.foundPath;
    }

    public List<PathNode> getFilterFoundPath() {
        return this.afterFilteFoundPath;
    }

    public boolean randomMapBlock(MapPoint point) {
        // 随机产生一个可以放玩家的位置的地图块;
        int i = (int) (Math.random() * this.width);
        int j = (int) (Math.random() * this.height);

        int x = i;
        int y = j;

        boolean finded = false;
        for (x = i; x < this.width; x++) {
            for (y = j; y < this.height; y++) {
                if (this.goMap[x][y]) {
                    finded = true;
                    break;
                }
            }

            if (finded) {
                break;
            }
            y = 0;
        }
        if (!finded) {
            y = 0;
            for (x = 0; x < i; x++) {
                for (; y < this.height; y++) {
                    if (this.goMap[x][y]) {
                        finded = true;
                        break;
                    }
                }

                if (finded) {
                    break;
                }
                y = 0;
            }

            if (!finded) {
                x = i;
                y = 0;
                for (; y < j; y++) {
                    if (this.goMap[x][y]) {
                        finded = true;
                        break;
                    }
                }
            }
        }
        // end

        if (finded) {
            point.setX(x);
            point.setY(y);
            return true;
        }

        return false;
    }

    public Vector3[] getFilterRoadData() {
        if (this.afterFilteFoundPath == null) {
            return null;
        }

        Vector3[] ret = new Vector3[this.afterFilteFoundPath.size()];
        for (int i = 0; i < this.afterFilteFoundPath.size(); i++) {
            ret[i] = new Vector3(this.afterFilteFoundPath.get(i).getIndexI() * size, this.ypos,
                this.afterFilteFoundPath.get(i).getIndexJ() * size);
        }

        return ret;
    }

    public Vector3[] getRoadData() {
        if (this.foundPath == null) {
            return null;
        }

        Vector3[] ret = new Vector3[this.foundPath.size()];
        for (int i = 0; i < this.foundPath.size(); i++) {
            ret[i] = new Vector3(foundPath.get(i).getIndexI() * size, this.ypos,
                foundPath.get(i).getIndexJ() * size);
        }

        return ret;
    }


    /**
     * @param GoMap  地图元数据
     * @param width  地图宽
     * @param height 地图高
     * @param Size   tile尺寸
     */
    public void initWithMapData(boolean[][] GoMap, int width, int height, float Size) {
        this.size = Size;
        this.goMap = GoMap;
        //创建开表，闭表
        openList = new ArrayList<>();
        closeList = new ArrayList<>();
        afterFilteFoundPath = new ArrayList<>();
        //从Tile里面获取路径信息
        this.createMapFromTile(width, height);
    }

    private void createMapFromTile(int width, int height) {
        this.width = width;
        this.height = height;
        pathMap = new PathNode[this.width][this.height];
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                //读取信息创建
                pathMap[i][j] = createGridFromTile(i, j);
            }
        }
    }

    private PathNode createGridFromTile(int i, int j) {
        PathNode p = new PathNode();
        p.setTraversable(goMap[i][j]);
        p.setIndexI(i);
        p.setIndexJ(j);
        return p;
    }

    public boolean isWordPosCanGo(Vector3 WorldPosition) {
        int Si, Sj;
        Si = Math.round(WorldPosition.getX() / this.size);
        Sj = Math.round(WorldPosition.getZ() / this.size);

        return this.goMap[Si][Sj];
    }

    public boolean astarSearch(Vector3 startPosition, Vector3 endPosition, float ypos) {
        int Si, Sj, Ei, Ej;
        Si = Math.round(startPosition.getX() / this.size);
        Sj = Math.round(startPosition.getZ() / this.size);

        Ei = Math.round(endPosition.getX() / this.size);
        Ej = Math.round(endPosition.getZ() / this.size);


        this.ypos = ypos;

        startGridPointer = pathMap[Si][Sj];
        targetGridPointer = pathMap[Ei][Ej];

        if (this.goMap[Ei][Ej]) {
            astarMapPathFind();
            return true;
        }

        return false;
    }

    private void astarMapPathFind() {
        //定义一个开表，其为需要评估的点
        //定义一个闭表，其为已经评估完了的点
        //将起始点加入到开表中
        this.clearLastFindMemory();
        openList.add(startGridPointer);
        //循环，直到开表中没有节点
        while (openList.size() > 0) {
            // 选择一个 当前点 当前点是开表中需要评估的点中，评估出来最接近目标点的点
            PathNode currentNode = findMinimalCostInOpenList();
            //将当前点从开表中移除并加入到闭表中
            openList.remove(currentNode);
            closeList.add(currentNode);
            //如果我们当前点就是目标点，那么结束寻路
            if (currentNode == targetGridPointer) {
                retracePath(startGridPointer, targetGridPointer);
                return;
            } else {
                //查找当前点的所有邻接点
                List<PathNode> Neiborgh = getNeighbour(currentNode);
                for (int i = 0; i < Neiborgh.size(); i++) {
                    //如果某个邻接点已经在闭表中（即评估过）或者是不可通过的则跳过这个邻接点
                    if (closeList.contains(Neiborgh.get(i)) || !Neiborgh.get(i).isTraversable()) {
                        continue;
                    }
                    float newGCost =
                        getDistance(currentNode, Neiborgh.get(i)) + currentNode.getGCost();
                    //如果从当前点到到其邻接点更短或邻接点不在开表中
                    if (newGCost < Neiborgh.get(i).getGCost() ||
                        !openList.contains(Neiborgh.get(i))) {
                        //那么计算邻接点的评估值
                        Neiborgh.get(i).setGCost(newGCost);
                        Neiborgh.get(i).setHCost(getDistance(Neiborgh.get(i), targetGridPointer));
                        Neiborgh.get(i)
                            .setTotalCost(Neiborgh.get(i).getGCost() + Neiborgh.get(i).getHCost());
                        //将其上一节点设置成当前节点
                        Neiborgh.get(i).setLastStepGrid(currentNode);
                        //如果邻接点不在开表中，就把他加如开表中
                        if (!openList.contains(Neiborgh.get(i))) {
                            openList.add(Neiborgh.get(i));
                        }
                    }
                }
            }
        }

    }

    private float getDistance(PathNode curGrid, PathNode tarGrid) {
        //H对角 = min(X方向差值, Y方向差值)
        //斜角行走消耗
        float H_diagonal = Math.min(Math.abs(curGrid.getIndexI() - tarGrid.getIndexI()),
            Math.abs(curGrid.getIndexJ() - tarGrid.getIndexJ()));
        //直线行走消耗
        //H直线  = X方向差值 + Y方向差值
        float H_straight = Math.abs(curGrid.getIndexI() - tarGrid.getIndexI()) +
            Math.abs(curGrid.getIndexJ() - tarGrid.getIndexJ());
        //H(n) = Dd*H对角 + Ds*(H直线-2H对角)
        float HCost = ddCost * H_diagonal + dsCost * (H_straight - 2 * H_diagonal);
        return HCost;
    }

    private List<PathNode> getNeighbour(PathNode inGrid) {
        List<PathNode> Neighbours = new ArrayList<PathNode>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }

                int checkX = inGrid.getIndexI() + x;
                int checkY = inGrid.getIndexJ() + y;

                if (checkX >= 0 && checkX < width && checkY >= 0 && checkY < height) {
                    Neighbours.add(pathMap[checkX][checkY]);
                }
            }
        }
        return Neighbours;
    }

    private PathNode findMinimalCostInOpenList() {
        PathNode path_Grid = openList.get(0);
        for (int i = 1; i < openList.size(); i++) {
            if (path_Grid.getTotalCost() >= openList.get(i).getTotalCost()) {
                path_Grid = this.openList.get(i);
            }
        }
        return path_Grid;
    }

    private boolean checkPointAllPass(int i, int j) {
        if (i + 1 == width || !goMap[i + 1][j]) {
            return false;
        }

        if (i == 0 || !goMap[i - 1][j]) {
            return false;
        }


        if (j + 1 == height || !goMap[i][j + 1]) {
            return false;
        }

        if (j == 0 || !goMap[i][j - 1]) {
            return false;
        }


        if (i + 1 == width || j + 1 == height || !goMap[i + 1][j + 1]) {
            return false;
        }

        if (i == 0 || j == 0 || !goMap[i - 1][j - 1]) {
            return false;
        }

        if (i == 0 || j + 1 == height || !goMap[i - 1][j + 1]) {
            return false;
        }

        return i != width && j != 0 && goMap[i + 1][j - 1];
    }

    private void filterPath() {
        this.afterFilteFoundPath.clear();
        this.afterFilteFoundPath.add(foundPath.get(0));
        if (foundPath.size() <= 1) {
            this.afterFilteFoundPath.add(foundPath.get(0));
            return;
        }

        int i = 1;
        for (i = 1; i < foundPath.size() - 1; i++) {
            if (!checkPointAllPass(foundPath.get(i).getIndexI(), foundPath.get(i).getIndexJ())) {
                this.afterFilteFoundPath.add(foundPath.get(i));
            }
        }

        this.afterFilteFoundPath.add(foundPath.get(i));
    }

    private void retracePath(PathNode startNode, PathNode endNode) {
        ArrayList<PathNode> path = new ArrayList<PathNode>();
        PathNode currentNode = endNode;

        while (currentNode != startNode) {
            path.add(currentNode);
            currentNode = currentNode.getLastStepGrid();
        }
        path.add(startGridPointer);
        Collections.reverse(path);
        foundPath = path;

        this.filterPath();
    }

    //清除上一次寻路对当前网格的影响
    private void clearLastFindMemory() {
        for (int j = 0; j < this.height; j++) {
            for (int i = 0; i < this.width; i++) {
                pathMap[i][j].setGCost(0);
                pathMap[i][j].setHCost(0);
                pathMap[i][j].setTotalCost(0);
                pathMap[i][j].setLastStepGrid(null);
            }
        }
        openList.clear();
        closeList.clear();
    }
}
