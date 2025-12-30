#!/usr/bin/env python3
"""Generate random non-overlapping tetrominoes (all 7 types with rotations).

Grid: 800x608 image, cell size 32x32 -> 25 cols x 19 rows.
Output file: 1-5/1.map with fixed header, flattened coords using flag 2, ending with " 128 512 3".
"""

import os
import random


IMAGE_W = 800
IMAGE_H = 608
CELL = 32


# Tetromino definitions with explicit rotations (offsets in grid cells, 0-based)
TETROMINOES = {
	"I": [
		[(0, 0), (1, 0), (2, 0), (3, 0)],
		[(0, 0), (0, 1), (0, 2), (0, 3)],
	],
	"O": [
		[(0, 0), (1, 0), (0, 1), (1, 1)],
	],
	"T": [
		[(0, 0), (1, 0), (2, 0), (1, 1)],
		[(0, 0), (0, 1), (0, 2), (1, 1)],
		[(1, 0), (0, 1), (1, 1), (2, 1)],
		[(0, 1), (1, 0), (1, 1), (1, 2)],
	],
	"S": [
		[(1, 0), (2, 0), (0, 1), (1, 1)],
		[(0, 0), (0, 1), (1, 1), (1, 2)],
	],
	"Z": [
		[(0, 0), (1, 0), (1, 1), (2, 1)],
		[(1, 0), (0, 1), (1, 1), (0, 2)],
	],
	"J": [
		[(0, 0), (0, 1), (1, 1), (2, 1)],
		[(0, 0), (1, 0), (0, 1), (0, 2)],
		[(0, 0), (1, 0), (2, 0), (2, 1)],
		[(1, 0), (1, 1), (1, 2), (0, 2)],
	],
	"L": [
		[(2, 0), (0, 1), (1, 1), (2, 1)],
		[(0, 0), (0, 1), (0, 2), (1, 2)],
		[(0, 0), (1, 0), (2, 0), (0, 1)],
		[(0, 0), (1, 0), (1, 1), (1, 2)],
	],
}


def _has_adjacent(cells, occu):
	"""Return True if any cell is adjacent (including diagonal) to occupied cells."""
	for col, row in cells:
		for dx in (-1, 0, 1):
			for dy in (-1, 0, 1):
				if dx == 0 and dy == 0:
					continue
				if (col + dx, row + dy) in occu:
					return True
	return False


def place_tetrominoes(count, image_w=IMAGE_W, image_h=IMAGE_H, cell=CELL, max_attempts=800):
	cols = image_w // cell
	rows = image_h // cell
	occu = set()  # occupied cells (col, row)
	all_cells = []  # list of (x, y) pixel coords

	if count < 0:
		raise ValueError("count must be non-negative")

	for _ in range(count):
		placed = False
		attempts = 0
		while not placed and attempts < max_attempts:
			attempts += 1
			name = random.choice(list(TETROMINOES.keys()))
			rotation = random.choice(TETROMINOES[name])
			max_dx = max(c[0] for c in rotation)
			max_dy = max(c[1] for c in rotation)
			if cols - (max_dx + 1) <= 0 or rows - (max_dy + 1) <= 0:
				raise ValueError("Grid too small for tetromino placement")
			base_col = random.randrange(0, cols - max_dx)
			base_row = random.randrange(0, rows - max_dy)

			cells = []
			overlap_or_adjacent = False
			for dx, dy in rotation:
				col = base_col + dx
				row = base_row + dy
				if (col, row) in occu:
					overlap_or_adjacent = True
					break
				cells.append((col, row))

			if overlap_or_adjacent:
				continue

			# Check adjacency (including diagonals) to existing pieces
			if _has_adjacent(cells, occu):
				continue

			# Accept placement
			for col, row in cells:
				x = col * cell
				y = row * cell
				all_cells.append((x, y))
				occu.add((col, row))
			placed = True

		if not placed:
			raise RuntimeError("Failed to place tetromino without overlap/adjacency after many attempts")

	return all_cells


def main():
	# User input: how many tetrominoes to place
	try:
		user_in = input("Enter the number of tetrominoes to place: ").strip()
		count = int(user_in) if user_in else 0
	except Exception:
		count = 0

	# Generate placements
	cells = place_tetrominoes(count)

	# Prepare output directory and path relative to this script
	base_dir = os.path.dirname(__file__)
	out_dir = os.path.join(base_dir, '1-5')
	os.makedirs(out_dir, exist_ok=True)
	out_path = os.path.join(out_dir, '1.map')

	# Build coordinate strings, using 2 to indicate a filled pixel (per requirement)
	coords = []
	for x, y in cells:
		coords.extend([str(x), str(y), '2'])

	with open(out_path, 'w', encoding='utf-8') as f:
		# Header lines required before coordinates
		f.write(" 1.030000\n")
		f.write("random_1\n")
		f.write("Jushi_Gen\n")

		# Write flattened coords as a single space-separated sequence (no trailing newline)
		f.write(' '.join(coords))

		# Final required line (exact formatting per request)
		f.write(" 128 512 3")


if __name__ == '__main__':
	main()
