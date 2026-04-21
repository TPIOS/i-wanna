#!/usr/bin/env python3
"""Generate colored tetromino sprites (32x32) with 3D border effects.

Creates 7 PNG files for each tetromino type using classic Tetris colors.
"""

from PIL import Image, ImageDraw
import os


# Soft pastel Tetris color scheme (outer light border, inner darker fill)
TETROMINO_COLORS = {
    "I": {"name": "Cyan", "inner": (120, 180, 200), "outer": (180, 220, 230)},
    "O": {"name": "Yellow", "inner": (200, 190, 120), "outer": (230, 220, 170)},
    "T": {"name": "Purple", "inner": (160, 130, 180), "outer": (200, 180, 210)},
    "S": {"name": "Green", "inner": (130, 180, 140), "outer": (180, 210, 180)},
    "Z": {"name": "Red", "inner": (200, 130, 140), "outer": (230, 180, 190)},
    "J": {"name": "Blue", "inner": (130, 150, 200), "outer": (180, 190, 230)},
    "L": {"name": "Orange", "inner": (210, 160, 120), "outer": (235, 200, 170)},
}


def create_tetromino_sprite(size=32, inner_color=(210, 160, 120), outer_color=(235, 200, 170)):
    """Create a single tetromino block sprite with soft border (outer light, inner dark)."""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    border_width = 4
    
    # Outer light border
    draw.rectangle([0, 0, size - 1, size - 1], fill=outer_color)
    
    # Inner darker fill
    draw.rectangle(
        [border_width, border_width, size - border_width - 1, size - border_width - 1],
        fill=inner_color
    )
    
    return img


def main():
    # Create output directory
    base_dir = os.path.dirname(__file__)
    out_dir = os.path.join(base_dir, 'tetromino_colors')
    os.makedirs(out_dir, exist_ok=True)
    
    print(f"Generating tetromino color sprites in: {out_dir}\n")
    
    # Generate each tetromino type
    for piece_type, colors in TETROMINO_COLORS.items():
        filename = f"{piece_type}_{colors['name']}.png"
        filepath = os.path.join(out_dir, filename)
        
        img = create_tetromino_sprite(
            size=32,
            inner_color=colors['inner'],
            outer_color=colors['outer']
        )
        
        img.save(filepath)
        print(f"✓ Created: {filename} ({colors['name']})")
    
    print(f"\nAll 7 tetromino color sprites generated successfully!")
    print(f"Location: {out_dir}")


if __name__ == '__main__':
    main()
