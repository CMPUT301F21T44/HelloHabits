# <a name="habit"></a>Class: Habit

| Responsibilities                      | Collaborators |
| ------------------------------------- | ------------- |
| know its name                         |               |
| know its reason                       |               |
| know the date that is started         |               |
| know the days that should be followed |               |

# <a name="habit-event"></a>Class: HabitEvent

| Responsibilities                       | Collaborators   |
| -------------------------------------- | --------------- |
| know the habit that it did             | [Habit](#habit) |
| know the date that this event happened | [Habit](#habit) |
| have an optional comment               |                 |
| have an optional photograph            |                 |
| have an geolocation where it take place|                 |

# <a name="profile"></a>Class: Profile

| Responsibilities                   | Collaborators              |
| ---------------------------------- | -------------------------- |
| know the habits that it owns       | [Habit](#habit)            |
| know the habit events that it owns | [HabitEvent](#habit-event) |
| know the habit events that it follows | [HabitEvent](#habit-event) |
