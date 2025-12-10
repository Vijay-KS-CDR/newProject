import 'package:flutter/material.dart';
import 'package:audioplayers/audioplayers.dart';
import '../models/song.dart';
import '../services/stats_service.dart';

class PlayerScreen extends StatefulWidget {
  const PlayerScreen({super.key});

  @override
  State<PlayerScreen> createState() => _PlayerScreenState();
}

class _PlayerScreenState extends State<PlayerScreen> {
  final AudioPlayer _audioPlayer = AudioPlayer();
  bool _isPlaying = false;
  Duration _duration = Duration.zero;
  Duration _position = Duration.zero;

  late Song _currentSong;
  bool _isInit = false;

  bool _isFav = false;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (!_isInit) {
      final args = ModalRoute.of(context)?.settings.arguments;
      if (args is Song) {
        _currentSong = args;
        _initAudio();
        _loadStats();
      }
      _isInit = true;
    }
  }

  void _loadStats() {
    setState(() {
      _isFav = StatsService().isFavorite(_currentSong.id);
    });
    // Increment play count when player opens (or wait for 30s in real app)
    StatsService().incrementPlayCount(_currentSong.id);
  }

  Future<void> _toggleFav() async {
    await StatsService().toggleFavorite(_currentSong.id);
    setState(() {
      _isFav = !_isFav;
    });
  }

  Future<void> _initAudio() async {
    if (_currentSong.data != null) {
      // Play local file
      await _audioPlayer.setSourceDeviceFile(_currentSong.data!);
    } else {
      // Url fallback
      await _audioPlayer.setSourceUrl(_currentSong.audioUrl);
    }
    
    // Listen to state
    _audioPlayer.onPlayerStateChanged.listen((state) {
      if (mounted) {
        setState(() {
          _isPlaying = state == PlayerState.playing;
        });
      }
    });

    // Listen to duration
    _audioPlayer.onDurationChanged.listen((d) {
      if (mounted) {
        setState(() {
          _duration = d;
        });
      }
    });

    // Listen to position
    _audioPlayer.onPositionChanged.listen((p) {
      if (mounted) {
        setState(() {
          _position = p;
        });
      }
    });

    // Auto play
    _audioPlayer.resume();
  }

  @override
  void dispose() {
    _audioPlayer.dispose();
    super.dispose();
  }

  String _formatTime(Duration d) {
    if (d == Duration.zero) return "--:--";
    final minutes = d.inMinutes;
    final seconds = d.inSeconds % 60;
    return '${minutes.toString().padLeft(2, '0')}:${seconds.toString().padLeft(2, '0')}';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        leading: IconButton(
          icon: const Icon(Icons.keyboard_arrow_down),
          onPressed: () => Navigator.pop(context),
        ),
        title: const Text('Now Playing'),
        centerTitle: true,
        actions: [
          IconButton(icon: const Icon(Icons.more_horiz), onPressed: () {}),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Album Art
            Container(
              height: 320,
              width: 320,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(20),
                color: Colors.grey[900], // Fallback color
                image: _currentSong.coverUrl.isNotEmpty 
                  ? DecorationImage(
                      image: NetworkImage(_currentSong.coverUrl),
                      fit: BoxFit.cover,
                    )
                  : null,
                boxShadow: [
                  BoxShadow(
                    color: Theme.of(context).primaryColor.withOpacity(0.4),
                    blurRadius: 30,
                    spreadRadius: 0,
                    offset: const Offset(0, 10),
                  ),
                ],
              ),
              child: _currentSong.coverUrl.isEmpty
                  ? const Icon(Icons.music_note, size: 80, color: Colors.white24)
                  : null,
            ),
            const SizedBox(height: 48),
            
            // Title & Artist
            Align(
              alignment: Alignment.centerLeft,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    _currentSong.title,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    _currentSong.artist,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(
                      fontSize: 18,
                      color: Colors.white60,
                    ),
                  ),
                ],
              ),
            ),
            
            const SizedBox(height: 32),

            // Progress Bar
            SliderTheme(
              data: SliderTheme.of(context).copyWith(
                thumbShape: const RoundSliderThumbShape(enabledThumbRadius: 0),
                overlayShape: const RoundSliderOverlayShape(overlayRadius: 0),
                trackHeight: 4,
                activeTrackColor: Theme.of(context).primaryColor,
                inactiveTrackColor: Colors.white24,
              ),
              child: Slider(
                min: 0,
                max: _duration.inSeconds.toDouble(),
                value: _position.inSeconds.toDouble().clamp(0, _duration.inSeconds.toDouble()),
                onChanged: (value) async {
                  final position = Duration(seconds: value.toInt());
                  await _audioPlayer.seek(position);
                  await _audioPlayer.resume();
                },
              ),
            ),
            
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 8.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(_formatTime(_position), style: const TextStyle(color: Colors.white60)),
                  Text(_formatTime(_duration), style: const TextStyle(color: Colors.white60)),
                ],
              ),
            ),

            const SizedBox(height: 24),

            // Controls
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                IconButton(
                  icon: Icon(
                    _isFav ? Icons.favorite : Icons.favorite_border, 
                    color: _isFav ? Colors.red : Colors.white60
                  ),
                  onPressed: _toggleFav,
                ),
                const Icon(Icons.skip_previous_rounded, color: Colors.white, size: 36),
                
                // Play/Pause Button
                GestureDetector(
                  onTap: () {
                    if (_isPlaying) {
                      _audioPlayer.pause();
                    } else {
                      _audioPlayer.resume();
                    }
                  },
                  child: Container(
                    height: 72,
                    width: 72,
                    decoration: BoxDecoration(
                      color: Theme.of(context).primaryColor,
                      shape: BoxShape.circle,
                      boxShadow: [
                        BoxShadow(
                          color: Theme.of(context).primaryColor.withOpacity(0.4),
                          blurRadius: 20,
                          spreadRadius: 2,
                        )
                      ]
                    ),
                    child: Icon(
                      _isPlaying ? Icons.pause_rounded : Icons.play_arrow_rounded,
                      size: 40,
                      color: Colors.white,
                    ),
                  ),
                ),
                
                const Icon(Icons.skip_next_rounded, color: Colors.white, size: 36),
                const Icon(Icons.repeat, color: Colors.white60),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
