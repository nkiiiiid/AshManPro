package net.noyark.DreamServerGroup;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.noyark.DreamServerGroup.actuators.Actuator;
import net.noyark.DreamServerGroup.actuators.E0Actuator;
import net.noyark.DreamServerGroup.actuators.E1Actuator;
import net.noyark.DreamServerGroup.actuators.E2Actuator;
import net.noyark.DreamServerGroup.command.EliminateCommand;
import net.noyark.DreamServerGroup.exception.EntityNotFindException;
import net.noyark.DreamServerGroup.placeholder.AshManProPlaceHolder;
import net.noyark.DreamServerGroup.placeholder.PlaceHolder;
import net.noyark.DreamServerGroup.triggers.T0Trigger;
import net.noyark.DreamServerGroup.triggers.T1Trigger;
import net.noyark.DreamServerGroup.triggers.T2Trigger;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class AshManPro extends PluginBase {

    public static final Map<Long, Entity> exemptedEntities = new Long2ObjectOpenHashMap<>();
    public static final Map<Integer, String> beforehand_message = new TreeMap<>((n1, n2) -> n2 - n1);
    public static boolean eliminate_entity = true;
    public static boolean eliminate_items = true;
    public static boolean eliminate_items_except_nbtcontains = false;
    public static boolean eliminate_entity_force = false;
    public static String terminate_message;
    private static AshManPro obj;
    private static Config config;
    private static Actuator actuator;
    private static Task task;
    private static PlaceHolder placeholder;

    public static Map<Long, Entity> getExemptedEntities() {
        return exemptedEntities;
    }

    public static Map<Integer, String> getBeforehandMessage() {
        return beforehand_message;
    }

    public static boolean isEliminateEntity() {
        return eliminate_entity;
    }

    public static boolean isEliminateItems() {
        return eliminate_items;
    }

    public static boolean isEliminateItemsExcept_nbtcontains() {
        return eliminate_items_except_nbtcontains;
    }

    public static boolean isEliminateEntityForce() {
        return eliminate_entity_force;
    }

    public static String getTerminateMessage() {
        return terminate_message;
    }

    public static PlaceHolder getPlaceholder() {
        return placeholder;
    }


    public static AshManPro getInstance() {
        return obj;
    }

    public static Actuator getActuator() {
        return actuator;
    }

    public static void addExemptedEntity(Entity entity) {
        addExemptedEntity(entity.getId(), entity);
    }

    public static void addExemptedEntity(long eid) {
        Entity entity;
        for (Level l : Server.getInstance().getLevels().values()) {
            if ((entity = l.getEntity(eid)) != null) {
                addExemptedEntity(eid, entity);
                return;
            }
        }
        throw new EntityNotFindException("无法找到对应实体id所对应的实体");
    }

    public static void addExemptedEntity(long eid, Entity entity) {
        exemptedEntities.put(eid, entity);
    }

    public static Entity removeExemptedEntity(long eid) {
        return exemptedEntities.remove(eid);
    }

    public static Entity removeExemptedEntity(Entity entity) {
        return exemptedEntities.remove(entity.getId());
    }

    public static void forceEliminate() {
        Server.getInstance().getLevels().values()
                .stream()
                .map(Level::getEntities)
                .flatMap(Arrays::stream)
                .filter(e -> e instanceof EntityCreature || e instanceof EntityItem)
                .forEach(Entity::close);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        obj = this;
        saveResource("config.yml");

        config = this.getConfig();

        eliminate_entity = config.getBoolean("eliminate-entity", true);
        eliminate_items = config.getBoolean("eliminate-items", true);
        eliminate_items_except_nbtcontains = config.getBoolean("eliminate-items-except-nbtcontains", false);
        eliminate_entity_force = config.getBoolean("eliminate-entity-force", false);

        terminate_message = config.getString("terminate-message", "清理信息获取失败");

        if (task != null) {
            task.cancel();
        }

        if (config.getBoolean("eliminate-trigger.0.enable")) {
            int di = config.getInt("eliminate-trigger.0.detect-interval") * 20;
            int threshold = config.getInt("eliminate-trigger.0.threshold");
            this.getServer().getScheduler().scheduleRepeatingTask(task = new T0Trigger(this, threshold), di);
            this.getServer().getLogger().info(TextFormat.GREEN + "当前检测器：T0");
        } else if (config.getBoolean("eliminate-trigger.1.enable")) {
            int di = config.getInt("eliminate-trigger.1.detect-interval") * 20;
            int threshold = config.getInt("eliminate-trigger.1.threshold");
            this.getServer().getScheduler().scheduleRepeatingTask(task = new T1Trigger(this, threshold), di);
            this.getServer().getLogger().info(TextFormat.GREEN + "当前检测器：T1");
        } else if (config.getBoolean("eliminate-trigger.2.enable")) {
            int di = config.getInt("eliminate-trigger.2.detect-interval") * 20;
            int entityThreshold = config.getInt("eliminate-trigger.2.entityThreshold");
            int AvgTPSThreshold = config.getInt("eliminate-trigger.2.AvgTPSThreshold");
            this.getServer().getScheduler().scheduleRepeatingTask(task = new T2Trigger(this, entityThreshold, AvgTPSThreshold), di);
            this.getServer().getLogger().info(TextFormat.GREEN + "当前检测器：T2");
        } else {
            this.getLogger().alert("没有检测器被开启");
        }

        if (config.getBoolean("eliminate-type.0.enable")) {
            actuator = new E0Actuator(config.getStringList("eliminate-type.0.eliminate-level-except"));
            this.getServer().getLogger().info(TextFormat.GREEN + "当前清理器：E0");
        } else if (config.getBoolean("eliminate-type.1.enable")) {
            actuator = new E1Actuator(config.getStringList("eliminate-type.1.eliminate-level-except"), config.getInt("eliminate-type.1.threshold"));
            this.getServer().getLogger().info(TextFormat.GREEN + "当前清理器：E1");
        } else if (config.getBoolean("eliminate-type.2.enable")) {
            actuator = new E2Actuator(
                    config.getDouble("eliminate-type.2.offsetX"),
                    config.getDouble("eliminate-type.2.offsetY"),
                    config.getDouble("eliminate-type.2.offsetZ"),
                    config.getStringList("eliminate-type.2.eliminate-player-except")
            );
            this.getServer().getLogger().info(TextFormat.GREEN + "当前清理器：E2");
        } else {
            this.getLogger().alert("没有清理器被开启");
        }

        Map m = config.getSection("beforehand-message");
        if (!m.isEmpty()) {
            Map<Integer, String> pm = new TreeMap<>((n1, n2) -> n2 - n1);
            m.forEach((k, v) -> pm.put((Integer) k, (String) v));
            beforehand_message.putAll(pm);
        }

        if (config.getBoolean("eliminate-entity-after-chunk-unload", false)) {
            this.getServer().getPluginManager().registerEvents(new ChunkEventListener(), this);
            this.getLogger().notice("区块卸载时删除区块内所有实体功能已开启");
        }
        this.getServer().getCommandMap().register("AshManPro", new EliminateCommand());
        placeholder = new AshManProPlaceHolder();
    }

    public static void broadcastMessage(String s, Object... os) {
        Server.getInstance().getOnlinePlayers().values().forEach(p -> {
            p.sendMessage(AshManPro.getPlaceholder().translate(s, p, os));
        });
    }
}
