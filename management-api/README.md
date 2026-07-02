# management-api

Feature Management Service — Management API. 负责 flag 的增删改查与发布流程,
把数据保存到 PostgreSQL,并承担 CDC 之前的"事实来源"角色。

技术栈:
- Spring Boot 3.3 / Java 21
- MyBatis Plus 3.5
- PostgreSQL + Flyway

## 快速开始

```bash
# 1. 准备一个 PostgreSQL 实例,创建空库
createdb feature_management

# 2. 启动(可由环境变量覆盖 datasource)
mvn spring-boot:run
```

启动后 Flyway 会执行 `V1__create_flags_table.sql`,自动建表。

## API 速览

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST   | `/api/v1/flags`                    | Create |
| PUT    | `/api/v1/flags/{key}`              | Update(乐观锁,客户端回传 `version`) |
| GET    | `/api/v1/flags`                    | Pagination, `?state=&flagType=&current=&size=` |
| GET    | `/api/v1/flags/{key}`              | Get one |
| DELETE | `/api/v1/flags/{key}`              | Soft delete(`state` → `archived`) |
| POST   | `/api/v1/flags/{key}/rollout`      | 调整 pct-flag 灰度比例 `0→10→50→100` |
| POST   | `/api/v1/flags/{key}/targeting`    | 替换 targeting-flag 的 rules |
| GET    | `/api/v1/explain?flag=...`         | Explainability(获取权威快照) |

> 跨域链路:管理服务写 PostgreSQL → Debezium CDC → Kafka → 各 region Consumer →
> Redis → SDK local cache (push + poll)。

## 数据库 Schema

```sql
flags (
  id, flag_key (UNIQUE), flag_type, state,
  definition (TEXT, JSON),
  version (BIGINT, optimistic lock),
  description, created_at, updated_at, created_by, updated_by
)
```

设计原则:
- 单一 `flags` 表,把可变结构(`definition`)放进 JSON 字段,新增 flag 类型不需要再 alter schema。
- 软删除(`state='archived'`)而不是真删,保留可解释性与审计能力。
- `version` 走 MyBatis Plus 的 `OptimisticLockerInnerInterceptor` 做乐观锁,
  PUT/Update 类接口必须回传最新 `version`。

## 示例 curl

```bash
# 创建一个 boolean flag
curl -X POST http://localhost:8080/api/v1/flags \
  -H 'Content-Type: application/json' \
  -d '{
    "flagKey":  "vip_discount",
    "flagType": "boolean",
    "definition": { "type": "true/false", "enabled": true },
    "description": "VIP 折扣总开关"
  }'

# 创建一个 pct-rollout flag
curl -X POST http://localhost:8080/api/v1/flags \
  -H 'Content-Type: application/json' \
  -d '{
    "flagKey":  "new_checkout",
    "flagType": "pct",
    "definition": { "type": "pct", "pct": 10, "salt": "a3f9",
                     "rules": [{ "attr": "role", "op": "eq", "value": "admin" }] }
  }'

# 渐进发布:10% → 50%
curl -X POST http://localhost:8080/api/v1/flags/new_checkout/rollout \
  -H 'Content-Type: application/json' \
  -d '{ "pct": 50, "salt": "a3f9" }'

# 编辑(乐观锁:version 必须回传)
curl -X PUT http://localhost:8080/api/v1/flags/new_checkout \
  -H 'Content-Type: application/json' \
  -d '{ "version": 2, "description": "已扩大至 50%" }'
```
