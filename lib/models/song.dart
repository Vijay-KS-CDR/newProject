class Song {
  final String id;
  final String title;
  final String artist;
  final String coverUrl;
  final String audioUrl;
  final String duration;
  final String? data; // Actual file path

  const Song({
    required this.id,
    required this.title,
    required this.artist,
    required this.coverUrl,
    required this.audioUrl,
    required this.duration,
    this.data,
  });

  // Dummy fallback
  static List<Song> get sampleSongs => [];
}
