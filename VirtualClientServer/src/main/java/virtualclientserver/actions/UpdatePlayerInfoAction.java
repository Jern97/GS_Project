package virtualclientserver.actions;

import classes.PlayerInfo;

import java.util.List;


public class UpdatePlayerInfoAction extends Action {
    private List<PlayerInfo> playerInfoList;

    public UpdatePlayerInfoAction(List<PlayerInfo> playerInfoList) {
        super(ActionType.UPDATE_PLAYER_INFO);
        this.playerInfoList = playerInfoList;
    }

    public List<PlayerInfo> getPlayerInfoList() {
        return playerInfoList;
    }

    public void setPlayerInfoList(List<PlayerInfo> playerInfoList) {
        this.playerInfoList = playerInfoList;
    }
}
