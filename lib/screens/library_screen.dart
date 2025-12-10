import 'package:flutter/material.dart';
import 'package:on_audio_query/on_audio_query.dart';
import 'package:permission_handler/permission_handler.dart';
import '../models/song.dart';
import '../widgets/song_tile.dart';
import '../services/stats_service.dart';

class LibraryScreen extends StatefulWidget {
  const LibraryScreen({super.key});

  @override
  State<LibraryScreen> createState() => _LibraryScreenState();
}

class _LibraryScreenState extends State<LibraryScreen> {
  final OnAudioQuery _audioQuery = OnAudioQuery();
  bool _hasPermission = false;

  @override
  void initState() {
    super.initState();
    _checkPermission();
  }

  Future<void> _checkPermission() async {
    // Check permission
    var status = await Permission.audio.status;
    if (status.isDenied || status.isRestricted || status.isPermanentlyDenied) {
       // Request permission
       status = await Permission.audio.request();
    }
    
    // Fallback for older Android versions
    if (await Permission.storage.isDenied) {
        await Permission.storage.request();
    }

    if (mounted) {
      setState(() {
        _hasPermission = true;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        title: const Text('Library'),
        backgroundColor: Colors.transparent,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () {},
          ),
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {},
          ),
        ],
      ),
      body: CustomScrollView(
        slivers: [
           SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Container(
                height: 140,
                decoration: BoxDecoration(
                  gradient: const LinearGradient(
                    colors: [Color(0xFF8B5CF6), Color(0xFF3B82F6)],
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                  ),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Stack(
                  children: [
                    Positioned(
                      left: 20,
                      top: 20,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'ExtractZ Premium',
                            style: Theme.of(context).textTheme.titleMedium?.copyWith(
                              color: Colors.white70,
                              fontWeight: FontWeight.bold
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            'Your Local Music',
                            style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    ),
                    const Positioned(
                      right: 20,
                      bottom: 20,
                      child: Icon(Icons.music_note, size: 48, color: Colors.white),
                    )
                  ],
                ),
              ),
            ),
          ),
          
          if (!_hasPermission)
             const SliverFillRemaining(
              child: Center(
                child: Text("Please grant storage permission to access music."),
              ),
            )
          else 
            FutureBuilder<List<SongModel>>(
              future: _audioQuery.querySongs(
                sortType: SongSortType.DATE_ADDED,
                orderType: OrderType.DESC_OR_GREATER,
                uriType: UriType.EXTERNAL,
                ignoreAsset: true,
              ),
              builder: (context, item) {
                if (item.data == null) {
                  return const SliverFillRemaining(child: Center(child: CircularProgressIndicator()));
                }
                if (item.data!.isEmpty) {
                  return const SliverFillRemaining(child: Center(child: Text("No songs found on device.")));
                }

                // Convert to our Song model
                final songs = item.data!.map((s) => Song(
                  id: s.id.toString(),
                  title: s.title,
                  artist: s.artist ?? "Unknown Artist",
                  coverUrl: "", // We can't get URL easily, handled in tile
                  audioUrl: s.uri ?? "",
                  duration: s.duration.toString(),
                  data: s.data
                )).toList();

                // Sort by Play Count (Descending)
                songs.sort((a, b) {
                  final countA = StatsService().getPlayCount(a.id);
                  final countB = StatsService().getPlayCount(b.id);
                  return countB.compareTo(countA);
                });

                return SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) {
                      return SongTile(
                        song: songs[index],
                        onTap: () {
                          Navigator.pushNamed(
                            context, 
                            '/player', 
                            arguments: songs[index]
                          );
                        },
                      );
                    },
                    childCount: songs.length,
                  ),
                );
              },
            ),
          // Pad bottom for miniplayer space if needed
          const SliverToBoxAdapter(child: SizedBox(height: 80)),
        ],
      ),
      bottomNavigationBar: BottomNavigationBar(
        backgroundColor: Colors.black.withOpacity(0.9),
        selectedItemColor: Theme.of(context).primaryColor,
        unselectedItemColor: Colors.grey,
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.library_music), label: 'Library'),
          BottomNavigationBarItem(icon: Icon(Icons.search), label: 'Search'),
          BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Profile'),
        ],
      ),
    );
  }
}
