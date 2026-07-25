package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.Account;
import org.mjc.entity.UserFriend;
import org.mjc.mapper.UserFriendMapper;
import org.mjc.service.AccountService;
import org.mjc.service.UserFriendService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好友关系服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriend> implements UserFriendService {

    @Resource
    private AccountService accountService;

    @Override
    public boolean requestFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null || userId.equals(friendId)) {
            return false;
        }

        // 检查是否已是好友
        if (isFriend(userId, friendId)) {
            log.warn("发送好友申请失败：已是好友 - userId={}, friendId={}", userId, friendId);
            return false;
        }

        // 检查是否已有待处理的申请（双向）
        LambdaQueryWrapper<UserFriend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFriend::getUserId, userId);
        wrapper.eq(UserFriend::getFriendId, friendId);
        wrapper.eq(UserFriend::getStatus, 0);
        if (this.count(wrapper) > 0) {
            log.warn("发送好友申请失败：已发送过申请 - userId={}, friendId={}", userId, friendId);
            return false;
        }

        // 检查对方是否已发来申请（如已发来，自动变成好友）
        LambdaQueryWrapper<UserFriend> reverseWrapper = new LambdaQueryWrapper<>();
        reverseWrapper.eq(UserFriend::getUserId, friendId);
        reverseWrapper.eq(UserFriend::getFriendId, userId);
        reverseWrapper.eq(UserFriend::getStatus, 0);
        UserFriend reverse = this.getOne(reverseWrapper, false);
        if (reverse != null) {
            // 双方互相申请，直接结为好友
            reverse.setStatus(1);
            reverse.setUpdateTime(LocalDateTime.now());
            this.updateById(reverse);
            log.info("双向申请自动结为好友: userId={}, friendId={}", userId, friendId);
            return true;
        }

        // 创建申请记录
        UserFriend userFriend = new UserFriend();
        userFriend.setUserId(userId);
        userFriend.setFriendId(friendId);
        userFriend.setStatus(0); // 待确认
        userFriend.setCreateTime(LocalDateTime.now());
        boolean result = this.save(userFriend);
        log.info("发送好友申请: userId={}, friendId={}, result={}", userId, friendId, result);
        return result;
    }

    @Override
    public boolean acceptFriend(Long id, Long friendId) {
        UserFriend userFriend = this.getById(id);
        if (userFriend == null || !userFriend.getFriendId().equals(friendId)) {
            log.warn("同意好友申请失败：记录不存在或无权限 - id={}, friendId={}", id, friendId);
            return false;
        }
        if (userFriend.getStatus() != 0) {
            log.warn("同意好友申请失败：已处理 - id={}, status={}", id, userFriend.getStatus());
            return false;
        }
        userFriend.setStatus(1);
        userFriend.setUpdateTime(LocalDateTime.now());
        boolean result = this.updateById(userFriend);
        log.info("同意好友申请: id={}, userId={}, friendId={}", id, userFriend.getUserId(), friendId);
        return result;
    }

    @Override
    public boolean rejectFriend(Long id, Long friendId) {
        UserFriend userFriend = this.getById(id);
        if (userFriend == null || !userFriend.getFriendId().equals(friendId)) {
            log.warn("拒绝好友申请失败：记录不存在或无权限 - id={}, friendId={}", id, friendId);
            return false;
        }
        if (userFriend.getStatus() != 0) {
            log.warn("拒绝好友申请失败：已处理 - id={}, status={}", id, userFriend.getStatus());
            return false;
        }
        userFriend.setStatus(2);
        userFriend.setUpdateTime(LocalDateTime.now());
        boolean result = this.updateById(userFriend);
        log.info("拒绝好友申请: id={}, userId={}, friendId={}", id, userFriend.getUserId(), friendId);
        return result;
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            return false;
        }
        // 双向删除：查询双向已确认的记录
        LambdaQueryWrapper<UserFriend> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .and(w1 -> w1.eq(UserFriend::getUserId, userId).eq(UserFriend::getFriendId, friendId))
                .or(w2 -> w2.eq(UserFriend::getUserId, friendId).eq(UserFriend::getFriendId, userId))
        );
        wrapper.eq(UserFriend::getStatus, 1);
        boolean result = this.remove(wrapper);
        if (result) {
            log.info("删除好友: userId={}, friendId={}", userId, friendId);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getFriendList(Long userId) {
        // 查询所有已确认的好友关系（双向）
        LambdaQueryWrapper<UserFriend> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .eq(UserFriend::getUserId, userId)
                .or().eq(UserFriend::getFriendId, userId)
        );
        wrapper.eq(UserFriend::getStatus, 1);

        List<UserFriend> relations = this.list(wrapper);
        List<Map<String, Object>> friendList = new ArrayList<>();

        for (UserFriend relation : relations) {
            Long friendId = relation.getUserId().equals(userId)
                    ? relation.getFriendId()
                    : relation.getUserId();

            Account account = accountService.getById(friendId);
            if (account == null) continue;

            Map<String, Object> item = new HashMap<>();
            item.put("friendId", friendId);
            item.put("username", account.getUname());
            item.put("realname", account.getRealname());
            item.put("avatar", account.getAvatar());
            friendList.add(item);
        }
        return friendList;
    }

    @Override
    public List<Map<String, Object>> getPendingRequests(Long userId) {
        LambdaQueryWrapper<UserFriend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFriend::getFriendId, userId);
        wrapper.eq(UserFriend::getStatus, 0);
        wrapper.orderByDesc(UserFriend::getCreateTime);

        List<UserFriend> requests = this.list(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();

        for (UserFriend request : requests) {
            Account account = accountService.getById(request.getUserId());
            if (account == null) continue;

            Map<String, Object> item = new HashMap<>();
            item.put("id", request.getId());
            item.put("userId", request.getUserId());
            item.put("username", account.getUname());
            item.put("realname", account.getRealname());
            item.put("createTime", request.getCreateTime());
            result.add(item);
        }
        return result;
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) return false;
        LambdaQueryWrapper<UserFriend> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .and(w1 -> w1.eq(UserFriend::getUserId, userId).eq(UserFriend::getFriendId, friendId))
                .or(w2 -> w2.eq(UserFriend::getUserId, friendId).eq(UserFriend::getFriendId, userId))
        );
        wrapper.eq(UserFriend::getStatus, 1);
        return this.count(wrapper) > 0;
    }
}
