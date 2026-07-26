package org.mjc.service;

import org.mjc.entity.ShopItem;
import java.util.List;
import java.util.Map;

public interface ShopService {

    /** 获取商城商品列表（上架的） */
    List<ShopItem> getItemList();

    /** 购买商品 */
    boolean purchase(Long userId, Long itemId);

    /** 获取用户已购买的物品 */
    List<Map<String, Object>> getUserItems(Long userId);

    /** 装备/卸下物品 */
    boolean equipItem(Long userId, Long itemId, boolean equip);

    /** 使用改名卡 */
    boolean useNameCard(Long userId, Long itemId, String newName);

    /** 使用置顶卡 */
    boolean useTopCard(Long userId, Long itemId, Long voteId);
}
