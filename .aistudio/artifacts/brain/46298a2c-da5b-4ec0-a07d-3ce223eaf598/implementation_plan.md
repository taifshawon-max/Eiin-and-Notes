# Bangladeshi School EIIN Finder & Class Notes Marketplace

A complete, production-ready Android application built with Kotlin and Jetpack Compose that empowers students, teachers, guardians, and administrators in Bangladesh to search verified School Educational Institute Identification Numbers (EIIN) and acquire academic class notes through a secure credit-based entitlement system.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions have been incorporated based on your requirements and the interactive testing environment:

- **Confirmed Decision 1 (Storage & Verification)**: Preloaded Room database with comprehensive Bangladeshi school records across all 8 divisions (Dhaka, Chattogram, Rajshahi, Khulna, Barishal, Sylhet, Rangpur, Mymensingh), structured note catalogue, user entitlement ledger, and transactional audit logs. Designed with clean repository abstractions to allow seamless Cloud Firestore mirroring.
- **Confirmed Decision 2 (Play Billing Architecture)**: Dual-layer billing system featuring:
  1. An interactive, authentic **Google Play Billing Sandbox Sheet** within the app emulator displaying exact Google Play UI, product token generation, pending state simulation, error testing, and server-grade receipt verification.
  2. Standard Play Billing client architecture ready for Play Console product IDs.
- **Confirmed Decision 3 (Zero-Double-Charge Entitlements)**: A one-credit per unique school/note model where unlocked schools and purchased notes are permanently recorded in user entitlements. Opening an already unlocked school or note never consumes additional credits.
- **Confirmed Decision 4 (Curated Dataset & Admin Tools)**: High-fidelity initial dataset of real Bangladeshi schools with valid 6-digit EIINs, board affiliations, district/upazila geographic hierarchy, accompanied by an in-app Admin Management Console to add/edit schools, import records, and upload PDF class notes.

---

## 1. Overview & Core Concept

### What It Does
- **School EIIN Finder**: Instant bilingual search (English and Bengali) for Bangladeshi high schools, colleges, and madrasahs. Search by school name or 6-digit EIIN with real-time suggestions, instant clipboard copy of EIIN, detailed institute profiles (Division, District, Upazila, Institute Type, Board, Status, and Verification Date).
- **Credit Entitlement Engine**: Users purchase school search credit packages ($1.01 for 100 schools up to $20.01 for 2,000 schools) and note packages ($1.01 for 1 note up to $20.01 for 20 notes). The app tracks total purchased, used, and remaining credits, with dedicated warnings when credits run low.
- **Academic Class Notes Marketplace**: Subject-wise catalog (Physics, Chemistry, Higher Math, Biology, ICT, Bangla, English) with course codes, academic year, contributor credits, free sample previews, and full built-in secure PDF reader for purchased notes.
- **Admin Dashboard**: Secure administrative tool for data imports, manual school record editing, note publishing, credit adjustments, and audit reports.

### Target Audience & Persona
- **Students & Admission Candidates**: Looking up accurate school codes (EIIN) for board registration, scholarship applications, and college admissions, while accessing high-yield class notes.
- **Teachers & Guardians**: Verifying institutional credentials and educational resources across Bangladesh's 64 districts.
- **Education Administrators**: Managing institute records and cataloging curriculum-aligned notes.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **EIIN Discovery Flow**:
   - Home / Search Tab -> Type school name or EIIN -> As-you-type fuzzy filtering -> Tap school card -> If already unlocked: instant full access; if new: confirmation prompt showing remaining credits -> Deduct 1 credit & unlock -> View full school dossier -> One-tap copy EIIN with haptic feedback.
2. **School Credit Purchase Flow**:
   - Tap "Get Credits" -> View package ladder (100 to 2,000 schools at `$N × $1.00 + $0.01`) -> Select package -> Review pricing modal -> Google Play Billing checkout sheet -> Order confirmation -> Idempotent transaction stored in ledger -> Credits incremented immediately.
3. **Class Note Preview & Purchase Flow**:
   - Class Notes Tab -> Filter by Subject / Class / Board -> Tap Note Card -> Read description & sample excerpt -> Tap "Unlock Full Note (1 Note Credit)" -> Confirmation dialog -> Note unlocked -> Built-in reading view with bookmarks and zoom.
4. **Admin Management Flow**:
   - Profile -> Admin Portal (Protected PIN / Role) -> Manage Schools (Add, Edit, Bulk Seed) -> Manage Notes -> View Transaction Logs & Data Quality metrics.

### Visual Identity & Theme
- **Theme**: Clean, prestigious Bangladeshi academic identity adhering to Material Design 3.
- **Primary Color**: Deep Academic Blue (`#0D47A1` / `#1565C0`) representing institutional trust.
- **Secondary / Accent**: Emerald Teal (`#00897B` / `#26A69A`) symbolizing growth and Bangladeshi education colors.
- **Background & Surfaces**: Crisp off-white (`#F8F9FA` / `#FFFFFF`) with dark surface tonal elevations (`#F1F5F9`).
- **Typography**: Clean hierarchy with bilingual support (Bangla & English) using readable weights and standard M3 scaling.
- **Micro-Interactions**: Smooth spring animations on bottom bar transitions, elevated card tap states with ripple feedback, and distinct badge indicators for unlocked items.

---

## 3. Key Product Decisions & Trade-Offs

| Decision | Chosen Approach | Why | Trade-off / Alternative Considered |
|---|---|---|---|
| **Data Engine** | Room Database with Room KSP + pre-seeded catalog | Guarantees instant search latency, full offline availability, and zero network dependency in the emulator | Pure Firestore requires active Google Cloud billing and network roundtrips for each keystroke. Room entity schema is structured to mirror Firestore documents. |
| **Search Indexing** | SQLite `LIKE` & tokenized prefix match across Name, EIIN, and District | Sub-10ms response time on device with live suggestions as the user types | External search services (Algolia/Typesense) add unnecessary cost and API key requirements for on-device apps. |
| **Billing Architecture** | Dual Google Play Billing Client + Sandbox Simulator Sheet | Allows end-to-end interactive purchase testing and receipt validation directly inside AI Studio without requiring physical Play Store credentials | Real Play Store testing requires APK upload to closed internal testing track in Play Console. |
| **PDF Handling** | Native Canvas/Compose Document Viewer with pagination & security controls | Completely offline, fast, prevents external scraping of paid notes, and works seamlessly in the container | Third-party webview PDF viewers can leak URLs or fail offline. |

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├───────────────────┬───────────────────┬──────────────────┬─────────────┤
│   Home Screen     │  EIIN Search Bar  │   Notes Market   │   Profile   │
│ & Credit Overview │   & School Detail │  & Document View │  & History  │
└─────────┬─────────┴─────────┬─────────┴────────┬─────────┴──────┬──────┘
          │                   │                  │                │
          ▼                   ▼                  ▼                ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      State & ViewModels (MVVM)                         │
│  • SchoolSearchViewModel   • NotesViewModel   • BillingViewModel       │
│  • UserProfileViewModel    • AdminViewModel                            │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                          Repository Layer                              │
│  • SchoolRepository    • NoteRepository    • EntitlementRepository     │
│  • BillingRepository (Play Billing Client & Receipt Validator)        │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                     Local Persistence (Room DB)                        │
│  • SchoolDao (Schools, Districts, Upazilas, EIINs)                     │
│  • NoteDao (Class Notes, Sample Pages, Full Content)                  │
│  • EntitlementDao (Unlocked Schools, Unlocked Notes, User Ledger)      │
│  • TransactionDao (Purchase Orders, Receipt Tokens, Audit Logs)        │
└────────────────────────────────────────────────────────────────────────┘
```

### Data Entities & Schema

1. **`SchoolEntity`**:
   - `id: String` (PK)
   - `name: String`, `nameBn: String`
   - `eiin: String` (Unique, 6-digit indexed)
   - `division: String`, `district: String`, `upazila: String`
   - `institutionType: String` (High School, College, School & College, Madrasah)
   - `board: String` (Dhaka, Rajshahi, Comilla, Chattogram, etc.)
   - `address: String`, `establishedYear: Int?`
   - `dataSource: String`, `lastVerifiedDate: String`

2. **`ClassNoteEntity`**:
   - `id: String` (PK)
   - `title: String`, `subject: String`, `courseCode: String`
   - `academicLevel: String` (Class 9-10 / SSC, Class 11-12 / HSC, Degree)
   - `author: String`, `fileFormat: String` ("PDF")
   - `description: String`, `sampleText: String`, `fullContent: String`
   - `totalPages: Int`, `priceCredits: Int` (1)

3. **`UserEntitlementEntity`**:
   - `id: String` (PK)
   - `schoolCreditsPurchased: Int`
   - `schoolCreditsUsed: Int`
   - `noteCreditsPurchased: Int`
   - `noteCreditsUsed: Int`
   - `lastUpdated: Long`

4. **`UnlockedSchoolEntity` & `UnlockedNoteEntity`**:
   - `schoolId / noteId: String` (PK)
   - `unlockedAt: Long`
   - `transactionId: String`

5. **`PurchaseTransactionEntity`**:
   - `transactionId: String` (PK)
   - `productType: String` ("SCHOOL_CREDITS" / "NOTE_CREDITS")
   - `productSku: String` (e.g. `school_credit_100`, `note_credit_5`)
   - `quantity: Int`
   - `priceUsd: Double` (Calculated by `$N × $1.00 + $0.01`)
   - `purchaseToken: String`
   - `timestamp: Long`
   - `status: String` ("COMPLETED", "PENDING", "REFUNDED")

---

## 5. Development Deliverables & Milestones

1. **Stage 1: Core Setup & Platform Identity**:
   - Update `metadata.json` and `strings.xml` with proper name: **"EIIN Finder & Notes"**.
   - Configure Room Database with KSP, Entities, DAOs, and seed database preloaded with authentic Bangladeshi school records across Dhaka, Chattogram, Rajshahi, Khulna, Sylhet, Barishal, Rangpur, and Mymensingh.
2. **Stage 2: Design System & Navigation Architecture**:
   - Implement M3 Theme (Academic Blue, Emerald Teal, crisp surfaces).
   - Bottom navigation structure: Home, Search, Notes, Profile.
3. **Stage 3: School EIIN Finder with Search & Credit Checks**:
   - Dynamic real-time search with partial name and EIIN matching.
   - Comprehensive detail sheet with EIIN copy, district metadata, and entitlement verification.
   - One-time credit consumption preventing duplicate charges.
4. **Stage 4: Class Notes Marketplace & Secure Document Reader**:
   - Card layout with filters (SSC, HSC, Science, Commerce, Arts).
   - Free preview reader and full content unlock with note credit consumption.
5. **Stage 5: Google Play Billing & Package Pricing Engine**:
   - Exact mathematical implementation of `$N × $1.00 + $0.01` formula for 100-2,000 schools and 1-20 notes.
   - Interactive Play Billing simulator sheet with token verification, status transitions, and purchase ledger recording.
6. **Stage 6: Admin Management Console & Self-Audit**:
   - Admin tool for adding/updating school records, reviewing data accuracy, managing notes, and inspecting transaction audit trails.
7. **Stage 7: Verification & Robolectric Tests**:
   - Verify complete compilation and test critical journeys (search, credit deduction, duplicate prevention, and billing).
