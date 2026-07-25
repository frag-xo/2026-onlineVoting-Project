package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.UserFriend;

import java.util.List;
import java.util.Map;

/**
 * 好友关系服务接口
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
public interface UserFriendService extends IService<UserFriend> {

    /**
     * 发送好友申请
     *
     * @param userId   当前用户ID
     * @param friendId 目标用户ID
     * @return 是否成功
     */
    boolean requestFriend(Long userId, Long friendId);

    /**
     * 同意好友申请
     *
     * @param id       好友关系ID
     * @param friendId 当前用户ID（接收方）
     * @return 是否成功
     */
    boolean acceptFriend(Long id, Long friendId);

    /**
     * 拒绝好友申请
     *
     * @param id       好友关系ID
     * @param friendId 当前用户ID（接收方）
     * @return 是否成功
     */
    boolean rejectFriend(Long id, Long friendId);

    /**
     * 删除好友
     *
     * @param userId   当前用户ID
     * @param friendId 好友用户ID
     * @return 是否成功
     */
    boolean deleteFriend(Long userId, Long friendId);

    /**
     * 获取好友列表（双向已确认的好友）
     *
     * @param userId 用户ID
     * @return 好友列表（含用户名、真实姓名等）
     */
    List<Map<String, Object>> getFriendList(Long userId);

    /**
     * 获取收到的好友申请列表
     *
     * @param userId 用户ID
     * @return 好友申请列表
     */
    List<Map<String, Object>> getPendingRequests(Long userId);

    /**
     * 检查两人是否为好友
     */
    boolean isFriend(Long userId, Long friendId);
}
