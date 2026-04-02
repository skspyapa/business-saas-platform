# Users vs Staff vs Customers - Entity Differentiation Guide

## Overview

The SaaS platform uses **three separate entities** to represent different personas in the system, each serving distinct purposes:

| Entity | Schema | Module | Purpose | Multi-tenant |
|--------|--------|--------|---------|--------------|
| **User** | tenant | module-tenant | Platform authentication & account management | ❌ No (Platform-wide) |
| **Staff** | transaction | module-transaction | Service providers/employees within a business | ✅ Yes (businessId) |
| **Customer** | core | module-core | Buyers/clients who purchase services/products | ✅ Yes (businessId) |

---

## 1. USER Entity

**Schema:** `tenant.users`  
**Module:** `module-tenant`  
**Scope:** Platform-wide (not multi-tenant)

### Purpose
- **Platform authentication** - Login credentials and account access
- **Identity management** - Core user profile for the entire platform
- **Permission base** - Used to assign roles and access levels across tenant businesses

### Key Attributes
```java
@Entity
@Table(name = "users", schema = "tenant")
public class User {
    UUID id;                    // Primary key
    String email;               // ✅ UNIQUE - Login identifier
    String passwordHash;        // Password for authentication
    String firstName;
    String lastName;
    String phoneNumber;
    Boolean isActive;           // Account active/suspended
    Boolean isEmailVerified;    // Email verification status
    
    // Audit fields
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
    Boolean isDeleted;
}
```

### Relationships
```
User (1) ──→ (Many) Business          [owner_id in businesses]
User (1) ──→ (Many) BusinessUserRole  [user_id in business_user_roles]
User (1) ──→ (Many) Staff             [user_id in staff]
User (1) ──→ (0..1) Customer          [userId in customers - optional]
```

### Real-World Examples
```
✓ John Doe (john@company.com) - Platform user account
  → Can log in to the platform
  → Can own multiple businesses
  → Can be a staff member in one business
  → Can be a customer in another business's shop

✓ Sarah Smith (sarah@company.com) - Platform user account
  → Can log in
  → Is manager of a salon business
  → Can serve as staff (hairdresser) in that business
```

### When to Use
- User registration/login
- Platform-wide authentication
- Access control configuration
- User profile management

---

## 2. STAFF Entity

**Schema:** `transaction.staff`  
**Module:** `module-transaction`  
**Scope:** Per-business (multi-tenant via businessId)

### Purpose
- **Service provider tracking** - Employees, contractors, service providers
- **Role assignment** - Job titles, availability, specializations
- **Transaction attribution** - Track who processed orders, appointments, refunds
- **Business operations** - Manage team members for a specific business

### Key Attributes
```java
@Entity
@Table(name = "staff", schema = "transaction")
public class Staff {
    UUID id;                        // Primary key
    UUID businessId;                // ✅ Multi-tenant - Which business owns this staff
    
    @ManyToOne
    User user;                      // 📌 Optional reference to User entity
    
    String firstName;
    String lastName;
    String email;                   // Staff email (can differ from User.email)
    String phoneNumber;
    String title;                   // Job title (Hairdresser, Manager, etc.)
    String bio;
    String avatarUrl;
    Boolean isActive;               // Currently employed
    Boolean isAvailable;            // Available to take appointments
    
    // Audit fields + businessId
}
```

### Relationships
```
Staff (Many) ──→ (1) Business        [businessId]
Staff (1) ──→ (0..1) User            [user_id - optional]
Staff (1) ──→ (Many) Appointment     [staff_id]
Staff (1) ──→ (Many) Refund          [processed_by]
```

### Real-World Examples
```
Business: "Beauty Haven Salon"

✓ Staff Member 1:
  - firstName: "Maria"
  - lastName: "Garcia"
  - title: "Senior Hairdresser"
  - businessId: uuid-of-beauty-haven
  - user: john_user  (linked to platform User)
  - isAvailable: true

✓ Staff Member 2:
  - firstName: "Alex"
  - lastName: "Kim"
  - title: "Receptionist"
  - businessId: uuid-of-beauty-haven
  - user: null  (NOT registered on platform, only exists as staff)
  - isAvailable: true

✓ Staff Member 3:
  - firstName: "Chris"
  - lastName: "Johnson"
  - title: "Manager"
  - businessId: uuid-of-different-business  (Different business)
  - user: different_user  (Chris is a different platform user)
```

### When to Use
- Creating/managing team members
- Assigning appointments to staff
- Tracking service provider credentials
- Recording who processed transactions
- Staff scheduling and availability
- Business operations management

---

## 3. CUSTOMER Entity

**Schema:** `core.customers`  
**Module:** `module-core`  
**Scope:** Per-business (multi-tenant via businessId)

### Purpose
- **Purchase tracking** - Record who buys products/services
- **Customer profiles** - Independent from platform users
- **Loyalty management** - Points, spending history, engagement
- **Order attribution** - Link purchases to customer records

### Key Attributes
```java
@Entity
@Table(name = "customers", schema = "core")
public class Customer {
    UUID id;                        // Primary key
    UUID businessId;                // ✅ Multi-tenant - Which business's customer
    
    UUID userId;                    // 📌 Optional reference to User (nullable)
    
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    
    // Loyalty & engagement tracking
    Integer loyaltyPoints;          // Rewards program points
    BigDecimal totalSpent;          // Customer lifetime value
    LocalDateTime lastPurchase;     // Recent purchase tracking
    
    // Audit fields + businessId
}
```

### Relationships
```
Customer (Many) ──→ (1) Business     [businessId]
Customer (0..1) ──→ (1) User         [userId - optional]
Customer (1) ──→ (Many) Order        [customer_id]
Customer (1) ──→ (Many) Appointment  [customer_id]
Customer (1) ──→ (Many) Payment      [customer_id]
Customer (1) ──→ (Many) Review       [customer_id]
```

### Real-World Examples
```
Business: "Beauty Haven Salon"

✓ Customer Type 1: Registered User → Customer
  - firstName: "Emma"
  - lastName: "Wilson"
  - email: "emma@gmail.com"
  - businessId: uuid-of-beauty-haven
  - userId: emma_user_id  (Emma has platform account)
  - loyaltyPoints: 250
  - totalSpent: $1,500.00
  - lastPurchase: 2024-03-28

✓ Customer Type 2: Walk-in / No Platform Account
  - firstName: "Michael"
  - lastName: "Brown"
  - email: "michael@gmail.com"
  - businessId: uuid-of-beauty-haven
  - userId: null  (NOT registered on platform)
  - loyaltyPoints: 45
  - totalSpent: $320.00
  - lastPurchase: 2024-03-25

✓ Customer Type 3: Same User, Different Business
  - firstName: "Sarah"
  - lastName: "Davis"
  - email: "sarah@gmail.com"
  - businessId: uuid-of-different-restaurant  (Different business)
  - userId: sarah_user_id  (Sarah is same platform user)
  - loyaltyPoints: 100
  - totalSpent: $450.00
  - lastPurchase: 2024-03-20
```

### When to Use
- Creating customer profiles
- Recording purchases
- Managing loyalty programs
- Tracking customer lifetime value
- Building customer history
- Personalization and recommendations

---

## 4. Key Differences - Quick Reference

### Identity vs. Role vs. Purchase

| Aspect | User | Staff | Customer |
|--------|------|-------|----------|
| **What it represents** | Platform identity | Service provider role | Purchaser role |
| **Required to login** | ✅ YES | ❌ NO (optional User link) | ❌ NO (optional User link) |
| **Multi-tenant (businessId)** | ❌ NO | ✅ YES | ✅ YES |
| **Per business** | ❌ One per platform | ✅ One per business | ✅ One per business |
| **Email** | ✅ Unique (login) | Per-staff email | Per-customer email |
| **Can have multiple roles** | ✅ YES (many businesses) | ✅ YES (if linked User) | ✅ YES (if linked User) |
| **Created by** | User self-registration | Business admin | Transaction system |
| **Purpose** | Authentication | Operations | Revenue tracking |

### Data Isolation

```
USERS (Platform-wide):
├── User 1: john@company.com
├── User 2: sarah@company.com
└── User 3: michael@company.com

STAFF (Per-Business):
Business A (Beauty Salon):
├── Staff 1: Maria Garcia (title: Hairdresser, user: john)
└── Staff 2: Alex Kim (title: Receptionist, user: null)

Business B (Restaurant):
├── Staff 1: Chris Johnson (title: Manager, user: michael)
└── Staff 2: Lisa Wong (title: Chef, user: null)

CUSTOMERS (Per-Business):
Business A (Beauty Salon):
├── Customer 1: Emma Wilson (userId: john - registered)
├── Customer 2: Michael Brown (userId: null - walk-in)
└── Customer 3: Sarah Davis (userId: null - walk-in)

Business B (Restaurant):
├── Customer 1: Sarah Davis (userId: michael - registered)
└── Customer 2: Tom Harris (userId: null - walk-in)
```

---

## 5. Entity Relationship Flows

### Flow 1: User Becomes Everything

```
Platform User Registration
  ↓
Create User (email, password)
  ↓
  ├─→ User logs into Business A
  │    ↓
  │    Assigned as Staff (Hairdresser)
  │    Assigned as Manager Role (RBAC)
  │
  ├─→ User logs into Platform
  │    ↓
  │    Browses Business A's services
  │    ↓
  │    Becomes Customer in Business A
  │    (Creates order for haircut appointment)
  │
  └─→ User can simultaneously be:
       • Staff in Business A
       • Customer in Business A  
       • Manager in Business B
       • Customer in Business B
```

### Flow 2: Staff Creation

```
Business Admin Action
  ↓
Create Staff Record
  ├─→ With User link (Optional)
  │    Staff: firstName, lastName, title, businessId, user_id
  │    (Staff can login via platform User account)
  │
  └─→ Without User link (Optional)
       Staff: firstName, lastName, title, businessId, user_id=null
       (Staff cannot login, managed internally)
```

### Flow 3: Customer Creation

```
Order/Appointment Creation
  ↓
Create Customer Record
  ├─→ From existing platform User (Registered)
  │    customer.userId = linked User ID
  │    (Customer can login and view order history)
  │
  └─→ Anonymous (Walk-in)
       customer.userId = null
       (Just a name and email, no platform login)
```

---

## 6. Use Case Scenarios

### Scenario 1: SaaS Multi-Tenant Business Platform

**Beauty Haven Salon** (Business A) operates on platform:

```
Tenant Data:
  Owner User: john@beautyhaven.com (Password: ***, isActive=true)

Transaction Data (Business A):
  Staff:
    - Maria Garcia (hairdresser, linked to User jane@beautyhaven.com)
    - Alex Kim (receptionist, no User link, created by admin)

  Customers:
    - Emma Wilson (userId: john - registered customer, loyal)
    - Michael Brown (userId: null - walk-in, call ahead)

  Orders (from customers):
    1. Emma → $150 haircut (via Staff: Maria)
    2. Michael → $50 haircut (via Staff: Maria)

  Appointments:
    1. Emma scheduled with Maria (staff_id, customer_id)
    2. Michael scheduled with Maria
```

### Scenario 2: Multi-Business User

```
Platform User: sarah@gmail.com

In Business A (Beauty Salon):
  ├─ Role: Staff (Hairdresser)
  │  └─ Can accept appointments
  │
  └─ Can be Customer
     └─ Can buy products/services

In Business B (Restaurant):
  ├─ Role: Customer
  │  └─ Can make reservations
  │
  └─ Not Staff
     └─ Cannot manage reservations
```

### Scenario 3: Guest Checkout

```
Walk-in Customer (No platform account):
  ├─ Customer created (firstName, lastName, email, phone)
  ├─ No User entity created (userId = null)
  ├─ Order created → linked to Customer
  ├─ Payment processed → linked to Customer
  └─ Later receives receipt email (not platform notification)

Later encounter:
  └─ Same person registers on platform
      → New User created
      → Old Customer records remain unlinked (userId = null)
      → Or optionally link existing Customer to new User
```

---

## 7. Database Schema Structure

### Tenant Schema (Platform Users)

```sql
CREATE TABLE tenant.users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,      -- Platform login
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  phone_number VARCHAR(20),
  is_active BOOLEAN DEFAULT true,
  is_email_verified BOOLEAN DEFAULT false,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  created_by VARCHAR(255),
  updated_by VARCHAR(255),
  is_deleted BOOLEAN DEFAULT false
);
```

### Core Schema (Customer Profiles)

```sql
CREATE TABLE core.customers (
  id UUID PRIMARY KEY,
  business_id UUID NOT NULL,               -- Multi-tenant
  user_id UUID,                            -- Optional User link
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  phone VARCHAR(20),
  address TEXT,
  loyalty_points INTEGER DEFAULT 0,
  total_spent NUMERIC(10,2) DEFAULT 0,
  last_purchase TIMESTAMP,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  created_by VARCHAR(255),
  updated_by VARCHAR(255),
  is_deleted BOOLEAN DEFAULT false,
  
  FOREIGN KEY (business_id) REFERENCES tenant.businesses(id),
  FOREIGN KEY (user_id) REFERENCES tenant.users(id) -- Optional
);
```

### Transaction Schema (Staff/Employees)

```sql
CREATE TABLE transaction.staff (
  id UUID PRIMARY KEY,
  business_id UUID NOT NULL,               -- Multi-tenant
  user_id UUID,                            -- Optional User link
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  phone_number VARCHAR(20),
  title VARCHAR(255),
  bio TEXT,
  avatar_url VARCHAR(500),
  is_active BOOLEAN DEFAULT true,
  is_available BOOLEAN DEFAULT true,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  created_by VARCHAR(255),
  updated_by VARCHAR(255),
  is_deleted BOOLEAN DEFAULT false,
  
  FOREIGN KEY (business_id) REFERENCES tenant.businesses(id),
  FOREIGN KEY (user_id) REFERENCES tenant.users(id) -- Optional
);
```

---

## 8. Why Are They Separate?

### ✅ Separation Benefits

| Benefit | Reason |
|---------|--------|
| **Flexible Identity** | Users don't need to be customers or staff |
| **Multi-tenant Data** | Staff and Customers are isolated per business; Users are not |
| **Independent Profiles** | A user can have customer profiles in multiple businesses |
| **Simplified Relationships** | Orders link to Customers, not Users (cleaner design) |
| **Scalability** | Different access patterns (Users: reads; Customers: updates) |
| **Business Logic** | Same User can be Staff and Customer in same business |
| **External Integrations** | Import customers without creating User accounts |

### ❌ What If They Were Combined?

```
❌ BAD: If User = Staff = Customer

Problems:
1. User must be created for every walk-in customer
   → Clutters user table with non-login accounts
   
2. Hard to track multi-business roles
   → User would need array of businesses (complexity)
   
3. Cannot be Staff in business A and Customer in business B
   → Would need complex role arrays
   
4. Orders must link to User, not business transaction context
   → Harder to isolate per-business data
   
5. Loyalty points would need per-business tracking
   → Would bloat User entity
   
6. Business data not properly isolated
   → Loses multi-tenant security guarantees
```

---

## 9. Decision Tree: Which Entity to Use?

```
Question: Who is this person?

├─ "She's logging into the platform"
│  └─ → USE: User Entity
│     └─ Store: email (unique), passwordHash, isActive
│
├─ "She's providing a service to our business"
│  └─ → USE: Staff Entity
│     └─ Store: businessId, title, isAvailable
│     └─ Link: user_id (if also platform login)
│
└─ "She's buying/using our services"
   └─ → USE: Customer Entity
      └─ Store: businessId, loyaltyPoints, totalSpent
      └─ Link: userId (if exists on platform, else null)
```

---

## 10. Common Operations

### Create a Platform User
```java
// Business: User signs up on platform
User user = new User();
user.setEmail("john@example.com");
user.setPasswordHash(hash);
user.setFirstName("John");
user.setLastName("Doe");
user.setIsActive(true);
user.setIsEmailVerified(false);
userRepository.save(user);
```

### Make User a Staff Member
```java
// Business: Admin adds user to staff roster
Staff staff = new Staff();
staff.setBusinessId(businessUuid);
staff.setUser(user);  // Link to platform User
staff.setFirstName("John");
staff.setTitle("Manager");
staff.setIsActive(true);
staffRepository.save(staff);
```

### Create a Customer (Registered)
```java
// Business: Registered User makes first purchase
Customer customer = new Customer();
customer.setBusinessId(businessUuid);
customer.setUserId(user.getId());  // Link to platform User
customer.setFirstName("John");
customer.setLastName("Doe");
customer.setEmail("john@example.com");
customer.setLoyaltyPoints(0);
customer.setTotalSpent(BigDecimal.ZERO);
customerRepository.save(customer);
```

### Create a Customer (Walk-in)
```java
// Business: Anonymous/walk-in customer
Customer customer = new Customer();
customer.setBusinessId(businessUuid);
customer.setUserId(null);  // No platform account
customer.setFirstName("Jane");
customer.setLastName("Smith");
customer.setEmail("jane@example.com");  // Just for receipt
customer.setLoyaltyPoints(0);
customer.setTotalSpent(BigDecimal.ZERO);
customerRepository.save(customer);
```

### Create Order (Link to Customer, not User)
```java
// Business: Customer places order
Order order = new Order();
order.setBusinessId(businessUuid);
order.setCustomer(customer);  // ✅ NOT user - links to Customer
order.setOrderNumber("ORD-2024-001");
order.setStatus("PENDING");
order.setTotalAmount(new BigDecimal("150.00"));
orderRepository.save(order);
```

---

## 11. Summary Table

| Dimension | User | Staff | Customer |
|-----------|------|-------|----------|
| **Entity Type** | Platform account | Business worker | Purchaser |
| **Primary Email** | Unique, for login | Per-business | Per-business |
| **Multi-tenant** | No (platform-wide) | Yes (businessId) | Yes (businessId) |
| **Can be optional** | No (account required) | Yes (no User link) | Yes (no User link) |
| **Authentication** | Enables login | No auth (if no User) | No auth (if no User) |
| **RBAC Roles** | Owner, Manager, Staff, etc. | Tracked in BusinessUserRole | N/A |
| **Operational Role** | Identity provider | Service provider | Revenue generator |
| **Relationships** | 1 User → Many Businesses | 1 Staff → 1 Business | 1 Customer → 1 Business |
| **Privacy** | Platform-wide | Business-scoped | Business-scoped |
| **Lifecycle** | Explicit signup | Admin creation or signup | Implicit (via order) |

---

## Conclusion

The three-entity model ensures:
- ✅ **Clean separation of concerns** - Authentication, Operations, Revenue
- ✅ **Proper multi-tenancy** - Customers and Staff isolated per business
- ✅ **Flexible relationships** - Same User can have multiple roles
- ✅ **Scalability** - Each entity optimized for its use case
- ✅ **Security** - Proper data isolation between businesses
- ✅ **Business logic** - Natural mapping to real-world scenarios

Use this guide when designing queries, APIs, and business logic!
