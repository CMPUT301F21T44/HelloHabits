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
| know the date that this event happened |                 |
| have an optional comment               |                 |
| have an optional photograph            |                 |

# <a name="profile"></a>Class: Profile

| Responsibilities                   | Collaborators              |
| ---------------------------------- | -------------------------- |
| know the habits that it owns       | [Habit](#habit)            |
| know the habit events that it owns | [HabitEvent](#habit-event) |
