# Lutemon Android Application Documentation

## Project Overview
Lutemon is a mobile game application that allows users to create, train, and battle creatures called Lutemons. Built using modern Android development practices, the game features an intuitive interface, strategic gameplay, and comprehensive statistics tracking.

## Technical Specifications
- Platform: Android
- Language: Java
- Minimum SDK: Android 11
- Build System: Gradle
- Architecture: Fragment-based single activity
- Version: 1.0

## Team and Task Distribution

### Team Members and Responsibilities

#### Boyue Zhang
- Core Game Logic:
  - Lutemon class implementation
  - Battle system mechanics including critical hits
  - Training system logic
  - Combat calculations
  - State management
- Documentation:
  - Technical documentation
  - API documentation
  - Code comments
- Testing:
  - Combat system testing
  - Game balance adjustments

#### Haochuan Cui
- UI/UX Development:
  - Custom view implementation
  - Fragment navigation system
  - Layout design and optimization
  - Animation systems
  - User interaction flows
- Data Management:
  - Storage system implementation
  - Import/Export functionality
  - Permission handling
  - Data persistence
  - Error recovery

#### Peizhe Han
- Statistics System:
  - Stats tracking implementation
  - Data visualization
  - Performance metrics
  - Chart generation
  - Real-time updates
- Testing:
  - Unit test development
  - Integration testing
  - Bug fixing
  - Performance monitoring

### Collaboration Workflow
1. Daily Coordination:
   - Morning standup meetings
   - Task progress updates
   - Blocker resolution

2. Code Integration:
   - UI/Logic integration (Haochuan & Boyue)
   - Stats/Storage integration (Peizhe & Haochuan)
   - Battle/Stats integration (Boyue & Peizhe)

3. Review Process:
   - Cross-review all major changes
   - Weekly code review meetings
   - Performance optimization discussions

## Core Features

### 1. Lutemon Creation and Management
- Create unique Lutemons with custom names
- Five distinct color types with unique base statistics:
  - White: Balanced (Attack: 5, Defense: 4, HP: 20)
  - Green: Defense-focused (Attack: 6, Defense: 3, HP: 19)
  - Pink: Attack-oriented (Attack: 7, Defense: 2, HP: 18)
  - Orange: High attack (Attack: 8, Defense: 1, HP: 17)
  - Black: Maximum attack (Attack: 9, Defense: 0, HP: 16)
- Procedurally generated unique shapes for visual identity

### 2. Training System
- Interactive training mechanics with location-based management
- Color-specific training bonuses:
  - White: +2 Attack per session
  - Green: +3 Attack per session
  - Pink: +4 Attack per session
  - Orange: +5 Attack per session
  - Black: +6 Attack per session
- Experience system:
  - Gain 1 experience point per training
  - Experience adds to attack power
  - Progress persists until defeat

### 3. Battle System
- Turn-based combat with strategic elements
- Combat mechanics:
  - Base damage: (Base Attack + Experience) - Defense
  - Critical hit system:
    * 25% chance for critical hit
    * Critical hits add 5 bonus damage
    * Critical hits announced in battle log
  - Health tracking and victory conditions
  - Detailed battle log with combat events
- Outcome handling:
  - Winners receive double experience
  - Losers reset to base statistics
  - Automatic home return for participants
- Battle area restrictions:
  - Two Lutemon maximum
  - Full health requirement
  - Location management

### 4. Statistics and Analytics
- Comprehensive tracking system:
  - Individual battle records
  - Training history
  - Statistical progression
- Visual data representation:
  - Attack growth charts
  - Experience progression
  - Performance metrics
- Real-time stat updates

### 5. Data Management
- Persistent storage system:
  - Automatic saving
  - State recovery
  - Data backup
- Import/Export functionality:
  - JSON format support
  - External storage access
  - Data validation

## System Architecture

### Component Diagram
```
+-------------+     +-----------+     +------------+
|    Views    |     |           |     |   Storage  |
| (Fragments) |<--->| MainActivity|<--->|    &      |
|  & Layouts  |     |           |     |   Data     |
+-------------+     +-----------+     +------------+
       ^                 ^                  ^
       |                 |                  |
       v                 v                  v
+-------------+     +-----------+     +------------+
|   Lutemon   |<--->|  Battle   |     | Statistics|
|   Entity    |     |  System   |     |  Tracking |
+-------------+     +-----------+     +------------+
```

### Key Classes

#### MainActivity
```java
public class MainActivity extends AppCompatActivity {
    private Storage storage;
    private NavController navController;
    private DataManager dataManager;
    
    // Navigation and state management
    // Permission handling
    // Data operations
}
```

#### Lutemon
```java
public class Lutemon implements Serializable {
    private final int id;
    private final String name;
    private final String color;
    private int attack;
    private int defense;
    private int experience;
    private int health;
    
    // Combat and training methods
    // State management
    // Visual representation
}
```

#### Storage
```java
public class Storage {
    private Map<String, List<Lutemon>> locationMap;
    private GlobalStats stats;
    
    // Location management
    // State persistence
    // Data coordination
}
```

### Data Flow
```
User Input → UI Layer → Business Logic → Data Management → Persistence
     ↑______________________________________________|
```

## Implementation Details

### Navigation System
- Bottom navigation bar with three main sections:
  - Home: Lutemon management
  - Training: Skill development
  - Battle: Combat arena
- Fragment-based screen management
- State-preserving navigation

### Custom Views
- LutemonShapeView: Shape rendering
- BattleArenaView: Combat visualization
- StatisticsView: Data visualization

### State Management
- Automatic state saving
- Runtime permission handling
- Error recovery system

## Installation and Usage

### Requirements
- Android 11 or higher
- Storage permissions
- 50MB free space

### Setup
1. Install APK
2. Grant permissions
3. Create first Lutemon
4. Begin training and battles

### Basic Usage
1. Create Lutemons in Home screen
2. Train in Training area
3. Battle in Battle arena with critical hit chances
4. Track progress in Statistics

## Support and Maintenance

### Bug Reporting
- Include device information
- Describe steps to reproduce
- Attach relevant screenshots

### Feature Requests
- Detail use cases
- Provide implementation suggestions
- Consider compatibility

### Contact
- Course: CT60A2412 Object-Oriented Programming
- Institution: LUT University
- Team Email: [Contact through course platform]