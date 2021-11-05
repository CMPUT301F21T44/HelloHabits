# <a name="habit"></a>Class: Habit

| Responsibilities                             | Collaborators              |
| -------------------------------------------- | -------------------------- |
| know its name                                |                            |
| know its reason                              |                            |
| know the date that is started                |                            |
| know the days that should be followed        |                            |
| know which events belong to it               | [HabitEvent](#habit-event) |
| calculate habit consistency using its events | [HabitEvent](#habit-event) |

# <a name="habit-event"></a>Class: HabitEvent

| Responsibilities                              | Collaborators   |
| --------------------------------------------- | --------------- |
| know the habit that it did                    | [Habit](#habit) |
| know the date that this event happened        | [Habit](#habit) |
| have an optional comment                      |                 |
| have an optional photograph                   |                 |
| have an optional location where it took place |                 |

# <a name="profile"></a>Class: Profile

| Responsibilities                      | Collaborators              |
| ------------------------------------- | -------------------------- |
| know the habits that it owns          | [Habit](#habit)            |
| know the habit events that it owns    | [HabitEvent](#habit-event) |
| know the habit events that it follows | [HabitEvent](#habit-event) |
| knows the users that it follows       | [Profile](#profile)        |

# <a name="social"></a>Class: Social

| Responsibilities                         | Collaborators                                                    |
| ---------------------------------------- | ---------------------------------------------------------------- |
| list the users that one can follow       | [Profile](#profile)                                              |
| request to follow another users habits   | [Profile](#profile), [Habit](#habit)                             |
| grant or deny follow requests            | [Profile](#profile)                                              |
| view others habits and visual indicators | [Profile](#profile), [Habit](#habit), [HabitEvent](#habit-event) |
