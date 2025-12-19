# ✨ CasterOS - Cross-Platform Magic Plugin

> **CasterOS** brings immersive, customizable spellcasting to your Minecraft server—supporting both Paper and Sponge!
> Define your own spells, use aliases, and enjoy a robust, error-friendly magic system.

---

## 🪄 Features

- **Dynamic Spells:**
  Define spells and aliases in a simple YAML file (`spells.yml`). No code required!

- **Magic Mode:**
  Players enter a focused state to cast spells—movement and actions are restricted for true immersion.

- **Custom Keybinds:**
  Choose your own magic activation key (swap hand, drop, sneak, interact, etc.).

- **Alias Support:**
  Cast spells using alternate names or phrases for a natural, roleplay-friendly experience.

- **Robust Error Feedback:**
  Every error (unknown spell, config issues, etc.) gives clear, actionable feedback to both players and admins.

- **Cross-Platform:**
  Works on both Paper and Sponge servers with a shared core logic.

---

## 📦 Getting Started

1. **Install:**
   - Drop the plugin into your `plugins` folder (Paper or Sponge).
2. **Configure Spells:**
   - Edit `spells.yml` to add or customize spells and aliases.
3. **Reload/Restart:**
   - Reload or restart your server to apply changes.
4. **Play:**
   - Use `/casteros` to check status, `/casteroskeybind` to set your magic key, and start casting!

---

## 📝 Example `spells.yml`

```yaml
fireball:
  type: fireball
  aliases:
    - flare
    - ignite
shield:
  type: shield
  aliases:
    - protect
    - barrier
```

---

## ❓ Commands

- `/casteros` — Show plugin status/info
- `/casteroskeybind` — Set your magic activation key

---

## 🛠️ Troubleshooting

- All errors are shown in chat and logged to console with error codes.
- If spells don’t load, check your `spells.yml` for formatting issues.

---

## 💡 Tips

- Encourage players to roleplay with creative spell aliases!
- Use the magic mode for duels, events, or adventure maps.

---

## 🌐 Supported Platforms

- Paper (1.17+)
- SpongeAPI 8+

---

**Unleash your server’s magical potential with CasterOS!**
