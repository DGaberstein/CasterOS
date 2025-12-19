# CasterOS

A cross-platform Minecraft magic system supporting Bukkit, Paper, Folia, and Sponge. 

## Project Structure
- **CasterOS-Core**: Core spell logic, data objects, and incantation parsing. No Minecraft API dependencies.
- **CasterOS-Paper**: Bukkit/Paper/Purpur/Folia implementation. Shades the Core module and includes Folia API.
- **CasterOS-Sponge**: Sponge implementation. Shades the Core module.

## Building
Use Gradle to build the platform-specific jars:

```
gradlew :CasterOS-Paper:shadowJar
```

or

```
gradlew :CasterOS-Sponge:shadowJar
```

## Next Steps
- Implement spell logic and player data in Core.
- Add platform-specific listeners and features in Paper and Sponge modules.
- See the checklist for full development phases.
