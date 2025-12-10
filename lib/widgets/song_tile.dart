import '../services/stats_service.dart';

class SongTile extends StatelessWidget {
  final Song song;
  final VoidCallback onTap;

  const SongTile({super.key, required this.song, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final playCount = StatsService().getPlayCount(song.id);
    final favIcon = StatsService().isFavorite(song.id) ? "❤️ " : "";

    return ListTile(
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      onTap: onTap,
      leading: Container(
        width: 50,
        height: 50,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(8),
          color: Colors.grey[800],
          image: song.coverUrl.isNotEmpty ? DecorationImage(
            image: NetworkImage(song.coverUrl),
            fit: BoxFit.cover,
          ) : null,
        ),
        child: song.coverUrl.isEmpty ? const Icon(Icons.music_note) : null,
      ),
      title: Text(
        song.title,
        style: const TextStyle(fontWeight: FontWeight.w600, color: Colors.white),
      ),
      subtitle: Text(
        '$favIcon${song.artist} • Played $playCount times',
        style: const TextStyle(color: Colors.white60),
      ),
      trailing: const Icon(Icons.more_vert, color: Colors.white60),
    );
  }
}
